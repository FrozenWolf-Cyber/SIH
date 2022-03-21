import requests
import time
## SIGNUP
# url = 'https://sih-smart-attendance.herokuapp.com/signup'
# ### mail_id user_name password name age address contact_no blood_grp
# embed = [0.01088425, -5.045874E-4, 0.00396495, -0.01736597, -0.012199042, -0.13732634, -0.008997835, 0.21264866, 0.027240675, -0.049011983, -0.0064786673, 3.6331784E-4, -0.003511148, 0.010835331, -0.001313814, 0.031845875, -0.007502277, -0.0057618925, 0.004631724, -0.0038881311, 0.08129743, -0.012162304, -0.0050120037, 0.005625201, 0.108825624, -7.8159675E-4, -0.005629309, -0.029014425, 0.062236324, -0.13333432, 0.00833215, -0.041104745, 0.15556912, -2.8930945E-4, 0.03935807, -0.16334367, 0.16403678, -0.014554362, 0.011986462, -0.052986477, -0.0017401387, 0.0012840605, 0.0025554295, -0.0030924247, 0.008330995, 0.055337075, -0.07688919, 0.006683934, -0.00798434, -0.01860657, 0.13817914, -0.0016484337, 0.010209333, -0.007009315, -0.041529916, 0.009610347, 0.15374412, -6.9326756E-4, -0.038858008, 0.008856142, -5.8862846E-4, -0.058973815, 0.04243582, -0.16354334, 0.0057518557, 0.24822517, -0.0069181556, 0.01998119, 0.007513638, 3.093531E-4, -2.8189115E-4, -0.36188176, -0.0016366562, 0.010012919, 0.0140404515, 0.012425434, -0.0027570287, -3.6115065E-4, 0.022379095, -0.11228308, 6.243959E-4, 0.060034942, -0.0038803548, 0.040131517, 0.10263493, -3.9223727E-4, 0.0022864128, -0.045649514, 0.008845831, -0.22372466, 0.06684422, -0.008438035, 6.371789E-4, 0.008163789, 0.074667856, -0.09990796, -0.0031025948, -0.09754587, -1.0679228E-5, -4.7419753E-4, 0.0035175856, -0.0036222069, -0.0013590564, -1.620583E-4, 0.0038536245, 0.0069965734, 0.093338765, 0.0069610314, 0.020498952, -0.0016549777, -0.045917228, 0.0014995725, -0.0048016002, 0.09941746, -0.0050954195, 0.12896271, -0.0014454824, -0.0060009006, 0.03399655, 0.18617912, -0.07615382, 0.01736307, -0.03836473, 6.241935E-4, 0.005501023, 0.0062744804, 0.0013671294, -0.008978406, 0.012567806, -0.13813987, -0.0042409212, -0.0021473777, 0.0016497655, 0.0026459415, -0.09259293, -0.0031494359, -0.09482127, 0.029441318, -0.015668029, 0.0054718847, -4.7086758E-4, -0.005597445, 0.0058053234, 0.14996414, -0.10269697, 0.22379701, -0.0230792, 0.006384757, -0.0010186309, 0.00659723, 0.0052311243, -0.051094282, 0.1157028, 9.354385E-4, 0.001090927, 0.013642381, -0.005497717, 0.02606288, 0.022546869, 0.001970596, -0.017699677, 0.0016664913, 0.009320345, -0.002604831, 0.0059226523, -4.4493578E-4, 0.0015995379, 0.30093092, 0.0028920118, -0.0042074695, 0.07452795, 0.080879845, 0.005261658, -0.087793715, -0.010570386, 0.0013304742, -0.025736043, -0.0034347922, -0.0030663898, 0.0014920274, -0.13549612, 0.115352474, -0.0113472855, 0.0016517816, -0.05712297, -0.13544352, -0.01161531, -0.058510117, 0.08189556, -0.12987365, -0.013357208, -0.004794394]

# embed = list(map(str, embed))
# data = {'mail_id' : 'p@gmail.com',
#         'user_name' : 'p',
#         'password' : 'sp',
#         'name' : 'Sundar Pichai',
#         'designation' : 'CEO',
#         'emp_no' : 'GOOG001',
#         'gender' : 'M',
#         'office_address' : 'India',
#         'contact_no' : '100',
#         'embed1' : embed,
#         'embed2' : embed,
#         'embed3' : embed,
#         }

# files = [('files', ("img", open("sp/sp_left.png", "rb"), 'image/png'))]

# x = requests.post(url, data= data, files=files)
# print(x.text)

## CHECK USERNAME
url = 'http://127.0.0.1:5000/check_username'
data = {'username' : 'p'}

x = requests.post(url, data= data)
print(x.text)

## LOGIN
url = 'http://127.0.0.1:5000/login'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_name_or_mail_id' : 'aad',
        'type_of_login' : 'username', #username or mail_id
        'password' : 'aad',}

x = requests.post(url, data= data)
print(x.text)

user_id = x.text

## GET INFO
url = 'http://127.0.0.1:5000/get_info'
data = {'user_id' : user_id}

x = requests.post(url, data= data)
print(x.text)

## GET EMBEDS
url = 'http://127.0.0.1:5000/get_embed'

data = {'user_id' : user_id}
x = requests.post(url, data= data)
print(x.text)

## UPDATE LOG
url = 'http://127.0.0.1:5000/update_log'
x = requests.post(url, data= {'user_id' : user_id, "check_in":"06.03.2022@10:53:56", "check_out":"blah-null"})
print(x.text)

x = requests.post(url, data= {'user_id' : user_id, "check_in":"blah-null","check_out":"06.03.2022@10:53:56"})
print(x.text)

x = requests.post(url, data= {'user_id' : user_id, "check_in":"06.03.2022@10:53:56", "check_out":"blah-null"})
print(x.text)
