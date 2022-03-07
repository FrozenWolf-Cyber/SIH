import os
import face_recognition
from psql_database import Database
from gdrive_wrapper import gdrive
from flask import Flask, request, Response

db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
db_user =  'postgres' #'be6a5ab891fb44'
db_psswrd = '3112003' #heroku-psswrd
db_name = 'sih_attendance' #heroku-db

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)
mydrive = gdrive()

app = Flask(__name__)

def start_verify(user_id, format):
    mydrive.download_user_img(user_id)
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

    clear_local_data(user_id)

def clear_local_data(user_id):
    for img in os.listdir(f"img_db/{user_id}"):
        os.remove(f"img_db/{user_id}/{img}")

    os.rmdir(f"img_db/{user_id}")


@app.route('/verify',methods = ['POST', 'GET'])
def verify():
    if request.method == 'POST':
        user_id = request.form['user_id']
        img = request.files['image']
        
        if not mydb.check_user_id_exist(user_id): # occurs only while testing or spamming
            return "NOPE"

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

@app.route('/check_in',methods = ['POST', 'GET'])
def check_in():
    if request.method == 'POST':
        user_id = request.form['user_id']
        mydb.check_in(user_id, request.form['date_time'])

        return "CHECKED IN"

@app.route('/check_out',methods = ['POST', 'GET'])
def check_out():
    if request.method == 'POST':
        user_id = request.form['user_id']
        mydb.check_out(user_id, request.form['date_time'])

        return "CHECKED OUT"

@app.route('/status',methods = ['POST', 'GET'])
def status():
    if request.method == 'POST':
        user_id = request.form['user_id']

        if not mydb.check_user_id_exist(user_id):
            return "NOPE"

        if user_id not in os.listdir("status"):
            return "NOT IN PATH"
            
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

        user_name_availablity, user_id = mydb.sign_up(tuple(data))
        if user_id is None:
            return "ALREADY IN USE"

        os.mkdir(f"img_db/{user_id}")
        for img_names in 'img_left img_right img_center'.split(' '):
            img = request.files[img_names]
            format = str(img).split('.')[-1].split("'")[0]
            img.save(f"img_db/{user_id}/{img_names}.{format}")
            img.close()

        mydrive.upload_img_folder(user_id)
        clear_local_data(user_id)

        return user_id

@app.route('/login',methods = ['POST', 'GET'])
def login():
    if request.method == 'POST':
        args = 'user_name_or_mail_id password'.split(' ')
        data = []
        for arg in args :
            data.append(request.form[arg])

        unique_id = mydb.user_login_details(data, type_of_login= request.form['type_of_login'])
        return unique_id

@app.route('/get_info',methods = ['POST', 'GET'])
def get_info():
    if request.method == 'POST':
        user_id = request.form['user_id']
        if not mydb.check_user_id_exist(user_id):
            return "NOPE"

        data_args = 'name,age,address,contact_no,blood_grp,log'.split(',')
        data = mydb.get_user_details(user_id)
        
        form = {}
        for i, j in zip(data_args, data):
            form[i] = j
            
        return form

@app.route('/master_cmd',methods = ['POST', 'GET'])
def master_cmd():
    if request.method == 'POST':
        output = mydb.master_command(request.form['cmd'])

        return output

@app.route('/gdrive_refresh',methods = ['POST', 'GET'])
def gdrive_refresh():
    if request.method == 'POST':
        mydrive.refresh()

        return "FINISHED REFRESHING"


if __name__ == '__main__':
    app.run(debug=False, threaded=True)