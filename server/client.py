import requests
import time

## VERIFY
url = 'http://127.0.0.1:5000/verify'
# url = 'https://pytorch-model.herokuapp.com/predict'

files = {'image': open('jeff2.jpg', 'rb')}

x = requests.post(url, files= files, data= {'user_id' : 'jeff'})

print(x.content)

while True:

    time.sleep(0.3)
    url = 'http://127.0.0.1:5000/status'
    x = requests.post(url, data= {'user_id' : 'jeff'})

    print(x.text)
    if x.text != 'WAIT':
        print(x.text)
        break

## SIGNUP
url = 'http://127.0.0.1:5000/signup'
### mail_id user_name password name age address contact_no blood_grp
data = {'mail_id' : 'gokul3112003.com@gmail.com',
        'user_name' : 'FrozenWolf',
        'password' : 'helloworld123',
        'name' : 'Gokul Adethya',
        'age' : '12',
        'address' : 'erode',
        'contact_no' : '100',
        'blood_grp' : 'B+'}

x = requests.post(url, data= data)
print(x.content)

## LOGIN
url = 'http://127.0.0.1:5000/login'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_name_or_mail_id' : 'gokul3112003.com@gmail.com',
        'type_of_login' : 'mail_id', #user_name or mail_id
        'password' : 'helloworld123',}

x = requests.post(url, data= data)
print(x.content)


## GET INFO
url = 'http://127.0.0.1:5000/get_info'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_id' : '16DwRiAu'}

x = requests.post(url, data= data)
print(x.content)
