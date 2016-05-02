# -*- coding: utf-8 -*-

#########################################################################
## This scaffolding model makes your app work on Google App Engine too
## File is released under public domain and you can use without limitations
#########################################################################

## if SSL/HTTPS is properly configured and you want all HTTP requests to
## be redirected to HTTPS, uncomment the line below:
# request.requires_https()

## app configuration made easy. Look inside private/appconfig.ini
from gluon.contrib.appconfig import AppConfig
## once in production, remove reload=True to gain full speed
myconf = AppConfig(reload=True)


if not request.env.web2py_runtime_gae:
    ## if NOT running on Google App Engine use SQLite or other DB
    db = DAL(myconf.take('db.uri'), pool_size=myconf.take('db.pool_size', cast=int), check_reserved=['all'])
else:
    ## connect to Google BigTable (optional 'google:datastore://namespace')
    db = DAL('google:datastore+ndb')
    ## store sessions and tickets there
    session.connect(request, response, db=db)
    ## or store session in Memcache, Redis, etc.
    ## from gluon.contrib.memdb import MEMDB
    ## from google.appengine.api.memcache import Client
    ## session.connect(request, response, db = MEMDB(Client()))

## by default give a view/generic.extension to all actions from localhost
## none otherwise. a pattern can be 'controller/function.extension'
response.generic_patterns = ['*'] if request.is_local else []
## choose a style for forms
response.generic_patterns = ['*']


## (optional) optimize handling of static files
# response.optimize_css = 'concat,minify,inline'
# response.optimize_js = 'concat,minify,inline'
## (optional) static assets folder versioning
# response.static_version = '0.0.0'
#########################################################################
## Here is sample code if you need for
## - email capabilities
## - authentication (registration, login, logout, ... )
## - authorization (role based authorization)
## - services (xml, csv, json, xmlrpc, jsonrpc, amf, rss)
## - old style crud actions
## (more options discussed in gluon/tools.py)
#########################################################################

from gluon.tools import Auth, Service, PluginManager

auth = Auth(db)
service = Service()
plugins = PluginManager()

## create all tables needed by auth if not custom tables
auth.define_tables(username=False, signature=False)

## configure email
mail = auth.settings.mailer
mail.settings.server = 'logging' if request.is_local else myconf.take('smtp.server')
mail.settings.sender = myconf.take('smtp.sender')
mail.settings.login = myconf.take('smtp.login')

## configure auth policy
auth.settings.registration_requires_verification = False
auth.settings.registration_requires_approval = False
auth.settings.reset_password_requires_verification = True

#########################################################################
## Define your tables below (or better in another model file) for example
##
## >>> db.define_table('mytable',Field('myfield','string'))
##
## Fields can be 'string','text','password','integer','double','boolean'
##       'date','time','datetime','blob','upload', 'reference TABLENAME'
## There is an implicit 'id integer autoincrement' field
## Consult manual for more options, validators, etc.
##
## More API examples for controllers:
##
## >>> db.mytable.insert(myfield='value')
## >>> rows=db(db.mytable.myfield=='value').select(db.mytable.ALL)
## >>> for row in rows: print row.id, row.myfield
#########################################################################

## after defining tables, uncomment below to enable auditing
# auth.enable_record_versioning(db)


# -*- coding: utf-8 -*-


#users table to store the information of all users who can access the app
db.define_table('users',
                db.Field('username','string',required=True,unique=True),
                db.Field('password','string',required=True),
                db.Field('name','string',required=True),
                db.Field('email','string',required=False,unique=True),
                db.Field('user_type','string',required=True),
                db.Field('hostel','string',required=False,default=""),
                db.Field('phone','string',required=False),
               )

#defining complaints table that stores all the complaints posted
db.define_table('complaints',
                db.Field('user_id','reference users',required=True),
                db.Field('complaint_type','integer',length=1,required=True),
                db.Field('concerned_user','reference users',required=True),
                db.Field('title','string',required=True),
                db.Field('description','string',required=True),
                db.Field('complaint_status','integer',default=0,length=1,required=False),
                db.Field('comments','list:reference comments',required=False,default=""),
                db.Field('comment_users','list:reference users',required=False,default=[]),
                db.Field('upvote_users','list:reference users',required=False,default=[]),
                db.Field('downvote_users','list:reference users',required=False,default=[]),
                db.Field('posted_at', 'datetime', default=request.now),
               )

#comments table stores all the coments with their respective complaint_id
db.define_table('comments',
                db.Field('user_id','reference users',required=True,default=0),
                db.Field('complaint_id','reference complaints',required=True),
                db.Field('description','string',required=True),
                db.Field('posted_at', 'datetime', default=request.now),
                )


#stores details of the logged in user
db.define_table('valid_user',
                db.Field('user_id','reference users',required=True),
               )

#this table of notifications stores notifications for all complaints,comments,upvotes,downvotes and resolve
db.define_table('notifications',
                db.Field('complaint_id','reference complaints',required=True),
                db.Field('description','string',required=True),
               )
