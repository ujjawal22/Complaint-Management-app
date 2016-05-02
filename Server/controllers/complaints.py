# -*- coding: utf-8 -*-
# try something like


##returns all complaints
def mainlist():
		return dict(complaints=db((db.complaints.id>0) & (db.users.id==db.complaints.user_id)).select(orderby=~db.complaints.id))

##returns all resolved complaints
def resolvedlist():
		return dict(complaints=db((db.complaints.id>0) & (db.complaints.complaint_status==1) & (db.users.id==db.complaints.user_id)).select(orderby=~db.complaints.id))

##returns all unresolved complaints
def unresolvedlist():
		return dict(complaints=db((db.complaints.id>0) & (db.complaints.complaint_status==0) & (db.users.id==db.complaints.user_id)).select(orderby=~db.complaints.id))

##returns information of a particular complaint
def complaint():
    x=request.args[0]
    if (db.complaints.id==x):
        u=db.complaints.user_id
        v=db.complaints.concerned_user
    else:
        0
    commentslist=db(db.comments.complaint_id==x).select()
    comment_users=[]
    ##stores all the comments for a particular complaint
    for comment in commentslist:
        commentuserid=comment.user_id
        comment_users=comment_users+[db(db.users.id==commentuserid).select()]
    return dict(complaints=db((db.complaints.id==x) & (db.users.id==u)).select(),concerned_user=db((db.complaints.id==x) & (db.users.id==v)).select(), valid_user=db(db.valid_user.id>0).select(),comments=commentslist,comment_users=comment_users)

##inserts new complaint to the complaints table
def new():
    if ("title" in request.vars) and ("description" in request.vars) and ("complaint_type" in request.vars):
        title = str(request.vars["title"]).strip()
        description = str(request.vars["description"]).strip()
        complaint_type1 = int(request.vars["complaint_type"])
        concerned_user1 = int(request.vars.concerned_user)
    user_id1 = db(db.valid_user.id>0).select().first().user_id
    username = db(db.users.id==user_id1).select().first().name
    concernedname = db(db.users.id==concerned_user1).select().first().name
    ##for x in range(22,24):
        ##db(db.complaints.id==x).delete()
    complainttype = ""
    if(complaint_type1==0):
        complainttype = "Individual Level"
    elif(complaint_type1==1):
        complainttype = "Hostel Level"
    elif(complaint_type1==2):
        complainttype = "Institute Level"
    complaint = db.complaints.insert(description=description,title=title,complaint_type=complaint_type1,concerned_user=concerned_user1,user_id=user_id1,)
    notification = username + " posted a new complaint \""+title+"\" to " + concernedname + "\nType: "+complainttype+ "\nPosted at: "+(complaint.posted_at).strftime("%Y-%m-%d_%H:%M:%S")
    db.notifications.insert(description=notification,complaint_id=complaint.id)
    return dict(success=True,complaints=db(db.complaints.id>0).select())


##inserts comment to the comments table and updates the complaints table for the particular complaint
def post_comment():
    if ("description" in request.vars) and ("complaint_id" in request.vars):
        description = str(request.vars["description"]).strip()
        complaint_id1 = int(request.vars["complaint_id"])
    user_id1 = db(db.valid_user.id>0).select().first().user_id
    username = db(db.users.id==user_id1).select().first().name
    title = db(db.complaints.id==complaint_id1).select().first().title
    value = db(db.complaints.id==complaint_id1).select().first().comment_users
    commentarray = db(db.complaints.id==complaint_id1).select().first().comments
    comment = db.comments.insert(description=description,user_id=user_id1,complaint_id=complaint_id1,)
    ##comment_user = db.comment_user.insert(user_id=user_id1,comment_id=comment.id,)
    db(db.complaints.id==complaint_id1).select().first().update_record(comment_users=value+[user_id1])
    db(db.complaints.id==complaint_id1).select().first().update_record(comments=commentarray+[comment.id])
    notification = username + " posted a new comment on \""+title+"\"\nCommented at: "+(comment.posted_at).strftime("%Y-%m-%d_%H:%M:%S")
    db.notifications.insert(description=notification,complaint_id=complaint_id1)
    return dict(success=True,complaints=db(db.complaints.id==complaint_id1).select(),user=db(db.users.id==user_id1).select(),comment=db(db.comments.id==comment.id).select())

##updates complaints table by changing the upvote_users entries
def upvote():
    x=request.args[0]
    user_id1 = int(db(db.valid_user.id>0).select().first().user_id)
    username = db(db.users.id==user_id1).select().first().name
    title = db(db.complaints.id==x).select().first().title
    value = db(db.complaints.id==x).select().first().upvote_users
    db(db.complaints.id==x).select().first().update_record(upvote_users=value+[user_id1])
    notification = username + " upvoted on complaint \""+title +"\""
    db.notifications.insert(description=notification,complaint_id=x,)
    return dict(success=True,updated_complaint=db(db.complaints.id==x).select())


##updates complaints table by changing the downvote_users entries
def downvote():
    x=request.args[0]
    user_id1 = int(db(db.valid_user.id>0).select().first().user_id)
    username = db(db.users.id==user_id1).select().first().name
    title = db(db.complaints.id==x).select().first().title
    value = db(db.complaints.id==x).select().first().downvote_users
    db(db.complaints.id==x).select().first().update_record(downvote_users=value+[user_id1])
    notification = username + " downvoted on complaint \""+title+"\""
    db.notifications.insert(description=notification,complaint_id=x,)
    return dict(success=True,updated_complaint=db(db.complaints.id==x).select())

##updates complaints table by changing the status of the particular complaint
def resolve():
    x=request.args[0]
    user_id1 = int(db(db.valid_user.id>0).select().first().user_id)
    username = db(db.users.id==user_id1).select().first().name
    title = db(db.complaints.id==x).select().first().title
    db(db.complaints.id==x).select().first().update_record(complaint_status=1)
    notification = username + " resolved the complaint \""+title+"\""
    db.notifications.insert(description=notification,complaint_id=x,)
    return dict(success=True,updated_complaint=db(db.complaints.id==x).select())

##inserts a new notification to the table with each new complaint,comment,upvote,downvote or resolve
def notifications():
    notifs = []
    for notification in db(db.notifications.id>0).select(orderby=~db.notifications.id):
        notifs = notifs + [notification,db(db.complaints.id==notification.complaint_id).select().first(),db(db.users.id==(db(db.complaints.id==notification.complaint_id).select().first()).user_id).select().first()]
    return dict(notifications=notifs)
