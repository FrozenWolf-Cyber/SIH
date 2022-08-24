import requests
import time
import random
import string
from PIL import Image
import io
import json
import threading


def random_string_generator(str_size):
    return ''.join(random.choice(string.ascii_letters) for x in range(str_size))

def random_number_generator(str_size):
    return ''.join(random.choice(string.digits) for x in range(str_size))

# URL = "https://sih-smart-attendance.herokuapp.com"
URL = "http://127.0.0.1:5000"

timmings = []

def do_analysis(nth_parallel):
    ## SIGNUP
    start = time.time()
    print(f"{nth_parallel} Testing SIGNUP......\n")
    url = f'{URL}/admin_signup'

    # mail_id = random_string_generator(6)+'@gmail.com'
    mail_id = 'gokul3112003.com@gmail.com'
    user_name = random_string_generator(7)
    psswrd = random_string_generator(5)
    contact_no = random_number_generator(5)

    data = {'mail_id' : mail_id,
            'name' : 'xxxSundar Pichai',
            'designation' : 'CEO',
            'gender' : 'M',
            'branch_name' : 'India',
            'contact_no' : contact_no,
            }

    files = [('files', ("img", open("sp/sp_left.png", "rb"), 'image/png'))]

    x = requests.post(url, data= data)
    emp_no = x.text
    print(x.text)
	
    embed = [[0.01088425, -5.045874E-4, 0.00396495, -0.01736597, -0.012199042, -0.13732634, -0.008997835, 0.21264866, 0.027240675, -0.049011983, -0.0064786673, 3.6331784E-4, -0.003511148, 0.010835331, -0.001313814, 0.031845875, -0.007502277, -0.0057618925, 0.004631724, -0.0038881311, 0.08129743, -0.012162304, -0.0050120037, 0.005625201, 0.108825624, -7.8159675E-4, -0.005629309, -0.029014425, 0.062236324, -0.13333432, 0.00833215, -0.041104745, 0.15556912, -2.8930945E-4, 0.03935807, -0.16334367, 0.16403678, -0.014554362, 0.011986462, -0.052986477, -0.0017401387, 0.0012840605, 0.0025554295, -0.0030924247, 0.008330995, 0.055337075, -0.07688919, 0.006683934, -0.00798434, -0.01860657, 0.13817914, -0.0016484337, 0.010209333, -0.007009315, -0.041529916, 0.009610347, 0.15374412, -6.9326756E-4, -0.038858008, 0.008856142, -5.8862846E-4, -0.058973815, 0.04243582, -0.16354334, 0.0057518557, 0.24822517, -0.0069181556, 0.01998119, 0.007513638, 3.093531E-4, -2.8189115E-4, -0.36188176, -0.0016366562, 0.010012919, 0.0140404515, 0.012425434, -0.0027570287, -3.6115065E-4, 0.022379095, -0.11228308, 6.243959E-4, 0.060034942, -0.0038803548, 0.040131517, 0.10263493, -3.9223727E-4, 0.0022864128, -0.045649514, 0.008845831, -0.22372466, 0.06684422, -0.008438035, 6.371789E-4, 0.008163789, 0.074667856, -0.09990796, -0.0031025948, -0.09754587, -1.0679228E-5, -4.7419753E-4, 0.0035175856, -0.0036222069, -0.0013590564, -1.620583E-4, 0.0038536245, 0.0069965734, 0.093338765, 0.0069610314, 0.020498952, -0.0016549777, -0.045917228, 0.0014995725, -0.0048016002, 0.09941746, -0.0050954195, 0.12896271, -0.0014454824, -0.0060009006, 0.03399655, 0.18617912, -0.07615382, 0.01736307, -0.03836473, 6.241935E-4, 0.005501023, 0.0062744804, 0.0013671294, -0.008978406, 0.012567806, -0.13813987, -0.0042409212, -0.0021473777, 0.0016497655, 0.0026459415, -0.09259293, -0.0031494359, -0.09482127, 0.029441318, -0.015668029, 0.0054718847, -4.7086758E-4, -0.005597445, 0.0058053234, 0.14996414, -0.10269697, 0.22379701, -0.0230792, 0.006384757, -0.0010186309, 0.00659723, 0.0052311243, -0.051094282, 0.1157028, 9.354385E-4, 0.001090927, 0.013642381, -0.005497717, 0.02606288, 0.022546869, 0.001970596, -0.017699677, 0.0016664913, 0.009320345, -0.002604831, 0.0059226523, -4.4493578E-4, 0.0015995379, 0.30093092, 0.0028920118, -0.0042074695, 0.07452795, 0.080879845, 0.005261658, -0.087793715, -0.010570386, 0.0013304742, -0.025736043, -0.0034347922, -0.0030663898, 0.0014920274, -0.13549612, 0.115352474, -0.0113472855, 0.0016517816, -0.05712297, -0.13544352, -0.01161531, -0.058510117, 0.08189556, -0.12987365, -0.013357208, -0.004794394]]
    embed = list(map(str, embed))
	
    url = f'{URL}/check_emp_no'
    data = {'emp_no' : emp_no}                                # SHOULD WORK
    x = requests.post(url, data= data)
    print(x.text)


    # print(embed)
    url = f'{URL}/signup'
    data = {'user_name' : user_name,
            'password' : psswrd,
            'emp_no': emp_no,
            'embed1' : embed,
            'embed2' : embed,
            'embed3' : embed,
            }

    x = requests.post(url, data= data, files=files)
    print(x.text)

    # CHECK OTP
    data = {'emp_no' : emp_no}   
    url = f'{URL}/send_otp'
    x = requests.post(url, data= data)
    print(x.text)
    otp = x.text

    url = f'{URL}/send_otp'
    x = requests.post(url, data= data)
    print(x.text)

    data = {'emp_no' : emp_no, 'otp':12}   
    url = f'{URL}/check_otp'
    x = requests.post(url, data= data)
    print(x.text)


    data = {'emp_no' : emp_no, 'otp':otp}   
    url = f'{URL}/check_otp'
    x = requests.post(url, data= data)
    print(x.text)


    # CHECK USERNAME
    print(f"\n{nth_parallel} Testing CHECK USERNAME......")
    url = f'{URL}/check_username'

    data = {'username' : random_string_generator(7)}
    x = requests.post(url, data= data)                              # ERROR
    print(x.text)

    data = {'username' : user_name}                                # SHOULD WORK
    x = requests.post(url, data= data)
    print(x.text)

	# CHECK EMPLOYEE NUMBER
    print(f"\n{nth_parallel} Testing CHECK EMPLOYEE NUMBER......")
    url = f'{URL}/check_emp_no'

    data = {'emp_no' : random_string_generator(7)}
    x = requests.post(url, data= data)                              # ERROR
    print(x.text)

    data = {'emp_no' : emp_no}                                # SHOULD WORK
    x = requests.post(url, data= data)
    print(x.text)


    ## LOGIN
    print(f"\n{nth_parallel} Testing LOGIN......")
    url = f'{URL}/login'

    ### mail_id user_name password name age address contact_no blood_grp    # ERROR
    data = {'user_name_or_mail_id' : random_string_generator(7),
            'type_of_login' : 'mail_id', #username or mail_id
            'password' : random_string_generator(7)}

    x = requests.post(url, data= data)
    print(x.text)

    ### mail_id user_name password name age address contact_no blood_grp    # SHOULD WORK
    data = {'user_name_or_mail_id' : mail_id,
            'type_of_login' : 'mail_id', #username or mail_id
            'password' : psswrd}

    x = requests.post(url, data= data)
    print(x.text)
    emp_no = x.text

    data = {'user_name_or_mail_id' : "ADMIN",
            'type_of_login' : "ADMIN", #username or mail_id
            'password' : "ADMIN"}

    x = requests.post(url, data= data)
    print(x.text)
    emp_no = x.text

    ### mail_id user_name password name age address contact_no blood_grp    # SHOULD WORK
    data = {'user_name_or_mail_id' : user_name,
            'type_of_login' : 'username', #username or mail_id
            'password' : psswrd}

    x = requests.post(url, data= data)
    print(x.text)
    emp_no = x.text

    data = {'user_name_or_mail_id' : random_string_generator(3),    # ERROR
            'type_of_login' : 'mail_id', #username or mail_id
            'password' : psswrd}

    x = requests.post(url, data= data)
    print(x.text)


    # ## GET EMBEDS                                        NEED TO REPLICATE THE APP BUT WORKS
    # print(f"\n{nth_parallel} Get Embeds......")
    # url = f'{URL}/get_embed'
    # data = {'emp_no' : emp_no}

    # x = requests.post(url, data= data)
    # print(json.loads(x.text).keys(), len(json.loads(x.text)['embed1']))


    ## UPDATE LOG
    print(f"\n{nth_parallel} Update Logs......")
    url = f'{URL}/update_log'
    x = requests.post(url, data= {'emp_no' : emp_no, "check_in":"06.03.2022@10:53:56", "check_out":"blah-null", "latitude":"123.1232", "longitude":"321.64533"})
    print(x.text)

    x = requests.post(url, data= {'emp_no' : emp_no, "check_in":"blah-null","check_out":"06.03.2022@10:53:56", "latitude":"64.34", "longitude":"81.92"})
    print(x.text)


    url = f'{URL}/update_log'
    x = requests.post(url, data= {'emp_no' : emp_no, "check_in":"06.03.2022@10:53:56", "check_out":"blah-null", "latitude":"70.56", "longitude":"128.109"})
    print(x.text)

    url = f'{URL}/check_in_out_status'
    x = requests.post(url, data= {'emp_no' : emp_no})
    print(x.text)

    url = f'{URL}/modify_log'
    x = requests.post(url, data= {'emp_no' : emp_no, "old_check_in":"06.03.2022@10:53:56", "old_check_out":"06.03.2022@10:53:56", "new_check_in":"9.26.2022@12:47:19", "new_check_out":"10.18.2022@17:53:56"})
    print(x.text)


    ## GET INFO
    print(f"\n{nth_parallel} Testing USER INFO......")
    url = f'{URL}/get_info' # ERROR
    data = {'emp_no' : '1'}

    x = requests.post(url, data= data)
    print(x.text)

    url = f'{URL}/get_info' # SHOULD WORK
    data = {'emp_no' : emp_no}

    x = requests.post(url, data= data)
    print(x.text)


    ## Branch name
    print(f"\n{nth_parallel} Get Branch nName......")
    url = f'{URL}/get_branch_info'

    x = requests.post(url, data= {'emp_no' : emp_no, 'branch_name':'Office1'})
    print(x.text)

    print(f"\n{nth_parallel} Get Image......")
    url = f'{URL}/get_img'

    x = requests.post(url, data= {'emp_no' : emp_no})
    img = io.BytesIO(x.content)
    a = Image.open(img)
    print(a.size)


    print(f"\n{nth_parallel} Checking website handling......")

    ## Get n Log Data
    url = f'{URL}/get_log_data'
    x = requests.post(url, data={'last_n_days':300})
    print(json.loads(x.text).keys())

    ## Get User Overview
    url = f'{URL}/get_user_overview'
    x = requests.post(url)
    print(json.loads(x.text).keys())

    timmings[nth_parallel] = time.time()-start



threads = []
for i in range(1):
    timmings.append(0)
    t = threading.Thread(target=do_analysis, args=[i])
    t.start()
    threads.append(t)

for thread in threads:
    thread.join()

print(f"MAX TIME FOR AN INSTANCE: {max(timmings)}")
print(f"MIN TIME FOR AN INSTANCE: {min(timmings)}")
print(f"AVG TIME FOR AN INSTANCE: {sum(timmings)/len(timmings)}")