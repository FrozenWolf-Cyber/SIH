import os
from flask import Flask, request, Response
from database import Database
import face_recognition

db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
db_user =  'root' #'be6a5ab891fb44'
db_psswrd = '3112003' #heroku-psswrd
db_name = 'SIH_attendance' #heroku-db

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)

app = Flask(__name__)

def start_verify(user_id, format):
    match = False
    for each_img in os.listdir('img_db/'+user_id):

        known_image = face_recognition.load_image_file(f'img_db/{user_id}/{each_img}')
        unknown_image = face_recognition.load_image_file(f'status/{user_id}/img.{format}')

        know_encoding = face_recognition.face_encodings(known_image)[0]
        unknown_encoding = face_recognition.face_encodings(unknown_image)[0]
        results = face_recognition.compare_faces([know_encoding], unknown_encoding)

        if results[0] == True:
            match = True
            break

    if match:
        os.mkdir(f"status/{user_id}/VERIFIED")

    else :
        os.mkdir(f"status/{user_id}/UNVERIFIED")


@app.route('/verify',methods = ['POST', 'GET'])
def verify():
    if request.method == 'POST':
        user_id = request.form['user_id']
        img = request.files['image']

        if user_id not in os.listdir(f"status"):
            os.mkdir(f"status/{user_id}")

        format = str(img).split('.')[-1].split("'")[0]
        img.save(f"status/{user_id}/img.{format}")

        wait = "WAIT"
        response = Response(wait)

        @response.call_on_close
        def on_close():
            start_verify(user_id, format)

        return response

        
@app.route('/status',methods = ['POST', 'GET'])
def status():
    if request.method == 'POST':
        user_id = request.form['user_id']
        directories = os.listdir(f"status/{user_id}")

        if len(directories) == 2:
            
            if "VERIFIED" in directories :
                os.rmdir(f"status/{user_id}/VERIFIED")
                directories.remove('VERIFIED')
                os.remove(f"status/{user_id}/{directories[0]}")
                os.rmdir(f"status/{user_id}")
                return "VERIFIED"

            os.rmdir(f"status/{user_id}/UNVERIFIED")
            directories.remove('UNVERIFIED')
            os.remove(f"status/{user_id}/{directories[0]}")
            os.rmdir(f"status/{user_id}")

            return "UNVERIFIED"
        else:
            return "WAIT"        
            
@app.route('/signup',methods = ['POST', 'GET'])
def signup():
    if request.method == 'POST':
        args = 'mail_id user_name password name age address contact_no blood_grp'.split(' ')
        data = []
        for arg in args :
            data.append(request.form[arg])

        user_name_availablity, unique_id = mydb.sign_up(tuple(data))
        return unique_id

@app.route('/login',methods = ['POST', 'GET'])
def login():
    if request.method == 'POST':
        args = 'user_name_or_mail_id password'.split(' ')
        data = []
        for arg in args :
            data.append(request.form[arg])

        unique_id = mydb.user_login_details(data, type_of_login= request.form['type_of_login'])
        return unique_id
        


if __name__ == '__main__':
   app.run(debug=False, threaded=True)