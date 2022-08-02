import requests

url = 'http://127.0.0.1:5000/get_log_data'

x = requests.post(url, data={'last_n_days':300})
print(x.text)

url = 'http://127.0.0.1:5000/get_user_overview'

x = requests.post(url)
print(x.text)
