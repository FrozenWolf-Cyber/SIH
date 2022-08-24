import databases
import sqlalchemy
import datetime

def convert_str_to_date(x):
    temp = x.split('@')
    return '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]

class Database:
    def __init__(self,host,user,passwd,database):
        DATABASE_URL = f'postgresql+psycopg2://{user}:{passwd}@{host}:5432/{database}'
 
        self.database = databases.Database(DATABASE_URL)
        self.metadata = sqlalchemy.MetaData()
        self.otp_expiry = 5 # minutes

        self.engine = sqlalchemy.create_engine(DATABASE_URL)
        self.metadata.create_all(self.engine)


        self.loc_database = [{'branch_name':'Office1','latitude': '10.83049819448061', 'longitude': '78.68589874213778'},
                           {'branch_name':'Office2', 'latitude':'19.0760', 'longitude':'72.8777'},
                           {'branch_name':'Office3', 'latitude':'28.7041', 'longitude':'77.1025'},
                           {'branch_name':'Office4', 'latitude': '22.5726','longitude': '88.3639'},
                           {'branch_name':'Office5', 'latitude': '13.0463', 'longitude': '80.1981'}
                          ]

    
    async def create(self, encryptor):
        await self.database.execute('''CREATE TABLE IF NOT EXISTS EMPLOYEE_DETAILS (
                          emp_no VARCHAR(100) UNIQUE ,
                          name VARCHAR(100) NOT NULL ,
                          mail_id VARCHAR(100) UNIQUE ,
                          designation VARCHAR(100), 
                          gender VARCHAR(100) NOT NULL ,
                          branch_name VARCHAR(100) NOT NULL ,
                          contact_no VARCHAR(100) NOT NULL ,
                          PRIMARY KEY (emp_no));
                          
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_INFO (
                          emp_no VARCHAR(100) UNIQUE ,
                          embed1 text[],
                          embed2 text[],
                          embed3 text[],
                          PRIMARY KEY (emp_no));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_LOGIN ( 
                          user_name VARCHAR(100) UNIQUE ,
                          password VARCHAR(100) NOT NULL ,
                          emp_no VARCHAR(100) UNIQUE ,
                          PRIMARY KEY (emp_no));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_LOG ( 
                          emp_no VARCHAR(100)  ,
                          check_in TIMESTAMP,
                          check_out TIMESTAMP,
                          in_latitude VARCHAR(100),
                          in_longitude VARCHAR(100),
                          out_latitude VARCHAR(100),
                          out_longitude VARCHAR(100));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS GEO_LOCATION ( 
                          branch_name VARCHAR(100)  ,
                          latitude VARCHAR(100),
                          longitude VARCHAR(100));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_IMG ( 
                          emp_no VARCHAR(100)  ,
                          data BYTEA,
                          PRIMARY KEY (emp_no));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS OTP (
                          emp_no VARCHAR(100),
                          otp VARCHAR (10),
                          creation TIMESTAMP);
        
        ''')

        await self.database.execute_many("INSERT INTO GEO_LOCATION (branch_name , latitude , longitude) SELECT * FROM (SELECT :branch_name , :latitude , :longitude) AS tmp WHERE NOT EXISTS (SELECT branch_name FROM GEO_LOCATION WHERE branch_name = :branch_name) LIMIT 1;", self.loc_database)
        
        # sample = pickle.load(open('employee_sample.pkl','rb')) 

        self.team_data = [{'emp_no': '1', 'name': 'Aadarsh', 'mail_id': 'aadarsh.ram@gmail.com', 'designation': 'ML', 'gender': 'M', 'branch_name': 'Office6', 'contact_no': '9488363342'},
                          {'emp_no': '2', 'name': 'Gokul', 'mail_id': 'gokul3112003.com@gmail.com', 'designation': 'ML', 'gender': 'M', 'branch_name': 'Office6', 'contact_no': '9384742775'},
                          {'emp_no': '3', 'name': 'Selva', 'mail_id': 'snsn010212@gmail.com', 'designation': 'APP', 'gender': 'M', 'branch_name': 'Office6', 'contact_no': '9003299917'},
                          {'emp_no': '4', 'name': 'Ashwanth', 'mail_id': 'ashwanth064@gmail.com', 'designation': 'APP', 'gender': 'M', 'branch_name': 'Office6', 'contact_no': '9940497154'},
                          {'emp_no': '5', 'name': 'Kavimalar', 'mail_id': 'kavimalar2508@gmail.com', 'designation': 'APP', 'gender': 'F', 'branch_name': 'Office6', 'contact_no': '6385768683'},
                          {'emp_no': '6', 'name': 'Venkatesh', 'mail_id': 'blackvenky21@gmail.com', 'designation': 'WEB', 'gender': 'M', 'branch_name': 'Office6', 'contact_no': '9543879507'},
                          
                          ]

        for i in range(len(self.team_data)):
            a = list(self.team_data[i].keys())
            a.remove('emp_no')
            for j in a:
                self.team_data[i][j] = encryptor.AES_encrypt(self.team_data[i][j])
                

        await self.database.execute_many("INSERT INTO EMPLOYEE_DETAILS (emp_no, name, mail_id, designation, gender, branch_name, contact_no) SELECT * FROM (SELECT :emp_no, :name, :mail_id, :designation, :gender, :branch_name, :contact_no) AS tmp WHERE NOT EXISTS (SELECT emp_no FROM EMPLOYEE_DETAILS WHERE emp_no = :emp_no OR mail_id = :mail_id) LIMIT 1;", self.team_data)
        


    async def generate_next_employee_no(self):
        result = await self.database.fetch_one("SELECT COUNT(emp_no) FROM EMPLOYEE_DETAILS")
        return str(int(tuple(result.values())[0])+1)


    async def check_emp_no_exist_master(self, emp_no):
        exist = False
        # print(f"SELECT id FROM USER_LOGIN WHERE id = '{user_id}'", flush=True)
        for i in await self.database.fetch_all(f"SELECT emp_no FROM  EMPLOYEE_DETAILS WHERE emp_no = '{emp_no}';"):
            i = tuple(i.values())
            if emp_no == i[0]:
                exist = True
                break

        return exist


    async def check_emp_no_signed(self, emp_no):
        exist = False
        # print(f"SELECT id FROM USER_LOGIN WHERE id = '{user_id}'", flush=True)
        for i in await self.database.fetch_all(f"SELECT emp_no FROM USER_LOGIN WHERE emp_no = '{emp_no}';"):
            i = tuple(i.values())
            if emp_no == i[0]:
                exist = True
                break

        return exist

    async def get_mail_id(self, emp_no):
        details = None

        print("SELECT mail_id FROM EMPLOYEE_DETAILS WHERE emp_no = '%s'" % (emp_no,), flush=True)
        for i in await self.database.fetch_all("SELECT mail_id FROM EMPLOYEE_DETAILS WHERE emp_no = '%s'" % (emp_no,)):
            i = tuple(i.values())
            details = i

        details = list(details)

        return details[0]


    async def save_otp(self, emp_no, otp):
        await self.database.execute("INSERT INTO OTP (emp_no , otp, creation) VALUES ('%s', '%s', current_timestamp)" % (emp_no, str(otp)))

        return "DONE"


    async def check_otp(self, emp_no, otp):
        await self.clear_otp()

        r = None
        for i in await self.database.fetch_all("SELECT emp_no, otp, creation  FROM OTP WHERE emp_no = '%s' AND otp = '%s'" % (emp_no, otp)):
            r = tuple(i.values())
            break

        if r is not None:
            await self.database.execute("DELETE FROM OTP WHERE emp_no = '%s'" % (emp_no))
            return True

        else:
            return False


    async def clear_otp(self):
        await self.database.execute("DELETE FROM OTP WHERE DATE_PART('minute', CURRENT_TIMESTAMP- creation) >= %s;" % (self.otp_expiry,))






    async def check_master_unique_data(self, data):  #(mail_id, contact no)
        check = True
        # d = await self.database.execute()
        for i in await self.database.fetch_all(f"SELECT mail_id , contact_no FROM EMPLOYEE_DETAILS WHERE contact_no = '{data[1]}' OR mail_id = '{data[0]}'"):
            #mail_id , user_nam
            i = tuple(i.values())
            if data[0] == i[0] or data[1] == i[1]:
                check = False
                break
        return check


    async def check_username(self, user_name):
        exist = False
        for i in await self.database.fetch_all(f"SELECT user_name FROM USER_LOGIN WHERE user_name = '{user_name}'"):
            i = tuple(i.values())
            if user_name == i[0]:
                exist = True
                break

        return exist



    async def add_db(self, data):
        user_name, password, emp_no, embed1, embed2, embed3 = data
        await self.database.execute("INSERT INTO USER_LOGIN (user_name , password, emp_no) VALUES ('%s', '%s', '%s')" % (user_name, password, emp_no))
        await self.database.execute("INSERT INTO USER_INFO (emp_no, embed1, embed2, embed3) VALUES ('%s', '{%s}', '{%s}', '{%s}')" % (emp_no, ', '.join(embed1), ', '.join(embed2), ', '.join(embed3)))
        return 1


    async def add_master_db(self, data):
        mail_id, name, designation, emp_no, gender, contact_no, branch_name = data
        await self.database.execute("INSERT INTO EMPLOYEE_DETAILS (mail_id, name, designation, emp_no, gender, contact_no, branch_name) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')" % (mail_id, name, designation, emp_no, gender, contact_no, branch_name))
        return 1


    async def get_embeds(self, emp_no):        
        for i in await self.database.fetch_all("SELECT embed1, embed2, embed3 FROM USER_INFO WHERE emp_no = '%s'"% (emp_no,)):
            details = list(i.values())
        return details


    # async def update_db(self, data):
    #     user_name, password, mail_id, name, designation, emp_no, gender, user_id, branch_name, contact_no = data
    #     await self.database.execute("UPDATE USER_LOGIN set mail_id = '%s', user_name = '%s', password = '%s' WHERE id = '%s'" % (mail_id, user_name, password, user_id))
    #     await self.database.execute("UPDATE USER_INFO set name = '%s', designation = '%s', emp_no = '%s', gender = '%s', branch_name = '%s', contact_no = '%s' WHERE id = '%s'" % (name, designation, emp_no, gender, branch_name, contact_no, user_id))
    #     return 1


    async def upload_img(self, emp_no, file):
        # print(psycopg2.Binary(file), flush=True)
        # print('\n\n========================================================\n\n',file, flush=True)
        await self.database.execute("INSERT INTO USER_IMG (emp_no, data) VALUES ('%s', %s::bytea)" % (emp_no, str(file)[1:]))


    async def get_img(self, emp_no):
        data = await self.database.fetch_all("SELECT data FROM USER_IMG WHERE emp_no = '%s'" % (emp_no,))
        return tuple(data[0].values())[0]



    async def admin_signup(self, data):
        mail_id, name, designation, gender, branch_name, contact_no = data
        user_name_availablity = await self.check_master_unique_data((mail_id, contact_no))
        emp_no = None
        if user_name_availablity:
            emp_no = await self.generate_next_employee_no()
            await self.add_master_db((mail_id, name, designation, emp_no, gender, contact_no, branch_name))

        return  user_name_availablity, emp_no



    async def signup(self, data):
        user_name, password, emp_no, embed1, embed2, embed3 = data
        user_name_availablity = await self.check_username(user_name)

        await self.add_db((user_name, password, emp_no, embed1, embed2, embed3))


        return  user_name_availablity, emp_no
    

    async def check_credentials(self, data, user_name_or_mail_id): # data = [username/mail_id, password]
        id_psswrd = None
        pswrd_incorrect = 0

        if user_name_or_mail_id == 'username' :
            for i in await self.database.fetch_all("SELECT emp_no , password FROM USER_LOGIN WHERE user_name = '%s' " % (data[0], ) ):
                i = tuple(i.values())
                id_psswrd = i

        elif user_name_or_mail_id == 'mail_id' :
            for i in await self.database.fetch_all("SELECT USER_LOGIN.emp_no , USER_LOGIN.password  FROM USER_LOGIN, EMPLOYEE_DETAILS  WHERE EMPLOYEE_DETAILS.mail_id = '%s' AND  USER_LOGIN.emp_no = EMPLOYEE_DETAILS.emp_no" % (data[0],) ):
                i = tuple(i.values())
                id_psswrd = i

        if id_psswrd is None:
            return "USERNAME/MAILID DOESN'T EXIST"

        elif len(id_psswrd) == 2: # emp_no, password
            if id_psswrd[1] == data[1]:
                return id_psswrd[0] # returns emp_no
            else :
                return  "INCORRECT PASSWORD" # incorrect password

        else:
            #username doesnt exist
            return "WHAT IN THE WORLD IS GOING ON"


    async def user_login_details(self, data, type_of_login):
        user_name_or_mail_id , password = data[0] , data[1]

        if type_of_login == 'username':
            response = await self.check_credentials((user_name_or_mail_id, password),'username')

        else:
            response = await self.check_credentials((user_name_or_mail_id, password),'mail_id')

        return response


    async def get_user_details(self, emp_no):
        details = None

        for i in await self.database.fetch_all("SELECT name, mail_id, designation, emp_no, gender, branch_name, contact_no FROM EMPLOYEE_DETAILS WHERE emp_no = '%s'" % (emp_no,)):
            i = tuple(i.values())
            details = i

        details = list(details)
        log_data = [[], [], [], [], [], []]
        
        for i in await self.database.fetch_all("SELECT check_in, check_out, in_latitude, in_longitude, out_latitude, out_longitude FROM USER_LOG WHERE emp_no = '%s'" % (emp_no,)):
            i = tuple(i.values())
            log_data[0].append(i[0])
            log_data[1].append(i[1])
            log_data[2].append(i[2])
            log_data[3].append(i[3])
            log_data[4].append(i[4])
            log_data[5].append(i[5])
        
        return details+log_data


    async def update_log(self, emp_no, check_in, check_out, latitude, longitude):
        # Input format : Date-Month-Year@Hour:Minute:Seconds
        # Required format : Year-Month-Date@Hour:Minute:Seconds
        # Location format : latitude@longitude


        if check_in is not None:
            temp = check_in.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKIN TIME IS IN WRONG FORMAT"

            check_in = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            await self.database.execute("INSERT INTO USER_LOG (emp_no, check_in, in_latitude, in_longitude) VALUES ('%s', '%s', '%s', '%s')" % (emp_no, check_in,latitude, longitude))

        else :
            temp = check_out.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKOUT TIME IS IN WRONG FORMAT"

            check_out = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            await self.database.execute("UPDATE USER_LOG set check_out = '%s', out_latitude = '%s', out_longitude = '%s' WHERE emp_no = '%s' AND check_out IS NULL" % (check_out, latitude, longitude, emp_no))
            
        return "LOG UPDATED"


    async def modify_log(self, emp_no, old_check_in, old_check_out, new_check_in, new_check_out):
        # Input format : Date-Month-Year@Hour:Minute:Seconds
        # Required format : Year-Month-Date@Hour:Minute:Seconds
        old_check_in, old_check_out, new_check_in, new_check_out = convert_str_to_date(old_check_in), convert_str_to_date(old_check_out), convert_str_to_date(new_check_in), convert_str_to_date(new_check_out)
        # print("UPDATE USER_LOG set check_in = '%s' check_out = '%s' WHERE id = '%s' AND check_in = '%s' AND check_out = '%s'" % (new_check_in, new_check_out, user_id, old_check_in, old_check_out),flush=True)
        await self.database.execute("UPDATE USER_LOG set check_in = '%s', check_out = '%s' WHERE emp_no = '%s' AND check_in = '%s' AND check_out = '%s'" % (new_check_in, new_check_out, emp_no, old_check_in, old_check_out))

        return "LOG MODIFIED"
        

    async def check_in_out_status(self, emp_no):
        results = await self.database.fetch_one("SELECT COUNT(*) FROM USER_LOG WHERE emp_no = '%s' AND check_out IS NULL" % (emp_no,))
        code = None

        if int(tuple(results.values())[0]) == 0:
            code = "CHECKED OUT"

        else :
            code = "CHECKED IN"

        return code


    async def get_branch_info(self, branch_name):
        coords = []

        for i in await self.database.fetch_all("SELECT latitude, longitude FROM GEO_LOCATION WHERE branch_name = '%s'" % (branch_name,)):
            i = tuple(i.values())
            coords = tuple(i)

        if len(coords) == 0:
            return "INCORRECT BRANCH NAME"

        return {"latitude": coords[0], "longitude": coords[1]}


    async def get_log_data(self, last_n_days):
        args = "emp_no check_in check_out in_latitude in_longitude out_latitude out_longitude".split(" ")
        data = {i:[] for i in args}

        for i in await self.database.fetch_all("SELECT emp_no, check_in, check_out, in_latitude, in_longitude, out_latitude, out_longitude FROM USER_LOG WHERE DATE_PART('day', CURRENT_TIMESTAMP- check_in) <= %s;" % (last_n_days,)):
            i = tuple(i.values())
            data['emp_no'].append(i[0])
            data['check_in'].append(i[1])
            data['check_out'].append(i[2])
            data['in_latitude'].append(i[3])
            data['in_longitude'].append(i[4])
            data['out_latitude'].append(i[5])
            data['out_longitude'].append(i[6])

        return data


    async def get_user_overview(self):
        args = "emp_no name designation gender branch_name".split(" ")
        data = {i:[] for i in args}

        for i in await self.database.fetch_all("SELECT EMPLOYEE_DETAILS.emp_no, EMPLOYEE_DETAILS.name, EMPLOYEE_DETAILS.designation, EMPLOYEE_DETAILS.gender, EMPLOYEE_DETAILS.branch_name FROM EMPLOYEE_DETAILS, USER_LOGIN where EMPLOYEE_DETAILS.emp_no = USER_LOGIN.emp_no"):
            i = list(i.values())
            for arg_, value in zip(args, i):
                 data[arg_].append(value)
                
        return data
        
