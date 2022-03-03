import os
import shutil
from flask import Flask, request, Response
import face_recognition

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
            

if __name__ == '__main__':
   app.run(debug=False, threaded=True)