##checks against the database for entered username and password and if exists it updates valid_user table with this user details
def login():
    username = request.vars.userid
    password = request.vars.password
    user = db((db.users.username==username) & (db.users.password==password)).select().first()
    try:
			db(db.valid_user.id>0).delete()
    except Exception, e:
            print "cannot clear"
    db.valid_user.insert(user_id=user)
    return dict(success=False if not user else True, user=user, valid_user = db(db.valid_user.id>0).select())
##logout api to exit the app
def logout():
    return dict(success=True,user=db(db.valid_user.id>0).select())

##inserts new user details in the users table
def new():
    name1 = str(request.vars["name"]).strip()
    username1 = str(request.vars["username"]).strip()
    password1 = str(request.vars["password"]).strip()
    phone1 = str(request.vars["phone"]).strip()
    email1 = str(request.vars["email"]).strip()
    hostel1 = str(request.vars["hostel"]).strip()
    usertype1 = str(request.vars["usertype"]).strip()
    newuser=db.users.insert(name=name1,username=username1,email=email1,user_type=usertype1,hostel=hostel1,password=password1,phone=phone1,)
    return dict(success=True,users=db(db.users.id==newuser.id).select())

@request.restful()
def api():
    response.view = 'generic.'+request.extension
    def GET(*args,**vars):
        patterns = 'auto'
        parser = db.parse_as_rest(patterns,args,vars)
        if parser.status == 200:
            return dict(content=parser.response)
        else:
            raise HTTP(parser.status,parser.error)
    def POST(table_name,**vars):
        return db[table_name].validate_and_insert(**vars)
    def PUT(table_name,record_id,**vars):
        return db(db[table_name]._id==record_id).update(**vars)
    def DELETE(table_name,record_id):
        return db(db[table_name]._id==record_id).delete()
    return dict(GET=GET, POST=POST, PUT=PUT, DELETE=DELETE)

##def clearcomplaints():
    ##try:
			##db(db.complaints.id>0).delete()
    ##except Exception, e:
            ##print "cannot clear"
    ##return dict(success=True)