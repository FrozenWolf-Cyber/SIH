import requests
import time

url = 'http://127.0.0.1:5000/verify'
# url = 'https://pytorch-model.herokuapp.com/predict'

files = {'image': open('jeff2.jpg', 'rb')}

x = requests.post(url, files= files, data= {'user_id' : 'jeff'})

print(x.text)

# while True:

#     time.sleep(0.3)
#     url = 'http://127.0.0.1:5000/status'
#     x = requests.post(url, data= {'user_id' : 'jeff'})

#     print(x.text)
#     if x.text != 'WAIT':
#         print(x.text)
#         break

