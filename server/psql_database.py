import psycopg2
import random
import string

class Database:
    def __init__(self,host,user,passwd,database):
        self.db = psycopg2.connect(host=host,
                                     user=user,
                                     password=passwd,
                                     database=database)


        self.cursor = self.db.cursor()

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_INFO (
                          id VARCHAR(20) UNIQUE ,
                          name VARCHAR(100) NOT NULL ,
                          designation VARCHAR(100), 
                          emp_no VARCHAR(20) NOT NULL ,
                          gender VARCHAR(20) NOT NULL ,
                          branch_name VARCHAR(100) NOT NULL ,
                          contact_no VARCHAR(20) NOT NULL ,
                          embed1 text[],
                          embed2 text[],
                          embed3 text[],
                          PRIMARY KEY (id));
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_LOGIN ( 
                          mail_id VARCHAR(100) UNIQUE ,
                          user_name VARCHAR(100) UNIQUE ,
                          password VARCHAR(100) NOT NULL ,
                          id VARCHAR(20) UNIQUE ,
                          PRIMARY KEY (id));
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_LOG ( 
                          id VARCHAR(20)  ,
                          check_in TIMESTAMP,
                          check_out TIMESTAMP);
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS GEO_LOCATION ( 
                          branch_name VARCHAR(100)  ,
                          latitude VARCHAR(20),
                          longitude VARCHAR(20));
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_IMG ( 
                          id VARCHAR(20)  ,
                          data BYTEA,
                          PRIMARY KEY (id));
        ''')

        self.branchinfo = [('Chennai', '13.0827', '80.2707'),
                           ('Mumbai', '19.0760', '72.8777'),
                           ('Delhi', '28.7041', '77.1025'),
                           ('Kolkata ', '22.5726', '88.3639'),
                          ]

        for (name_, latitude_, longitude_) in self.branchinfo:
            self.cursor.execute('INSERT INTO GEO_LOCATION (branch_name, latitude, longitude) VALUES (%s, %s, %s)', (name_, latitude_, longitude_,))

        self.cursor.close()
        self.db.commit()
        self.cursor = self.db.cursor()
        

    def generate_user_id(self): # unique id varies from 8-15 characters
        unique_id = ''
        for i in range(random.randrange(8,15)): 
            unique_id = unique_id + random.choice(string.printable[:61])

        return unique_id

    def check_user_id_exist(self, user_id):
        exist = False
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT id FROM USER_LOGIN WHERE id = %s",(user_id,))
        for i in self.cursor:
            if user_id == i[0]:
                exist = True
                break

        self.cursor.close()
        return exist

    def check_unique_data(self, data):
        check = True
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT mail_id , user_name FROM USER_LOGIN")
        for i in self.cursor:
            #mail_id , user_nam
            if data[0] == i[0] or data[1] == i[1]:
                check = False
                break
        self.cursor.close()
        return check

    def check_username(self, user_name):
        exist = False
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT user_name FROM USER_LOGIN WHERE user_name = %s",(user_name,))
        for i in self.cursor:
            if user_name == i[0]:
                exist = True
                break

        self.cursor.close()
        return exist



    def add_db(self, data):
        self.cursor = self.db.cursor()  
        user_name, password, mail_id, name, designation, emp_no, gender, user_id, branch_name, contact_no, embed1, embed2, embed3 = data
        self.cursor.execute('INSERT INTO USER_LOGIN (mail_id , user_name , password , id) VALUES (%s, %s, %s, %s)',(mail_id, user_name, password, user_id))
        self.cursor.execute('INSERT INTO USER_INFO (id , name , designation, emp_no , gender, branch_name , contact_no , embed1, embed2, embed3) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)',(user_id, name, designation, emp_no, gender, branch_name, contact_no, embed1, embed2, embed3))
        # self.cursor.execute('INSERT INTO USER_LOG (id) VALUES (%s)', (user_id,))
        
        self.cursor.close()
        self.db.commit()
        return 1

    def get_embeds(self, user_id):
        details = None
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT embed1, embed2, embed3 FROM USER_INFO WHERE id = %s", (user_id,))

        for i in self.cursor:
            details = i

        details = list(details)

        self.cursor.close()
        self.cursor = self.db.cursor()
        
        return details

    def update_db(self, data):
        self.cursor = self.db.cursor()  
        user_name, password, mail_id, name, designation, emp_no, gender, user_id, branch_name, contact_no = data
        self.cursor.execute('UPDATE USER_LOGIN set mail_id = %s, user_name = %s, password = %s WHERE id = %s',(mail_id, user_name, password, user_id))
        self.cursor.execute('UPDATE USER_INFO set name = %s, designation = %s, emp_no = %s, gender = %s, branch_name = %s, contact_no = %s WHERE id = %s',(name, designation, emp_no, gender, branch_name, contact_no, user_id))
    
        self.cursor.close()
        self.db.commit()
        return 1

    def upload_img(self, user_id, file):
        self.cursor = self.db.cursor()  
        self.cursor.execute('INSERT INTO USER_IMG (id, data) VALUES (%s, %s)', (user_id,file))
        self.cursor.close()
        self.db.commit()   

    def get_img(self, user_id):
        self.cursor = self.db.cursor()  
        self.cursor.execute('SELECT data FROM USER_IMG WHERE id = %s', (user_id,))
        data = self.cursor.fetchall()
        self.cursor.close()

        return data[0][0]

    def sign_up(self, data):
        mail_id, user_name, password, name, designation, emp_no, gender, branch_name, contact_no, embed1, embed2, embed3 = data
        user_name_availablity = self.check_unique_data((mail_id, user_name))
        unique_id = None
        if user_name_availablity:
            while True:
                unique_id = self.generate_user_id()
                if self.check_user_id_exist(unique_id):
                    continue
                
                else :
                    self.add_db((user_name, password, mail_id, name, designation, emp_no, gender, unique_id, branch_name, contact_no, embed1, embed2, embed3))
                    break

        return  user_name_availablity, unique_id

    
    def check_credentials(self, data, user_name_or_mail_id): # data = [username, password]
        id_psswrd = None
        pswrd_incorrect = 0
        self.cursor = self.db.cursor()  

        if user_name_or_mail_id == 'username' :
            self.cursor.execute('SELECT id , password FROM USER_LOGIN WHERE user_name = %s ', (data[0], ) )
            for i in self.cursor:
                id_psswrd = i

        elif user_name_or_mail_id == 'mail_id' :
            self.cursor.execute('SELECT id , password  FROM USER_LOGIN WHERE mail_id = %s ', (data[0],) )
            for i in self.cursor:
                id_psswrd = i

        self.cursor.close()

        if id_psswrd is None:
            return "USERNAME/MAILID DOESN'T EXIST"

        elif len(id_psswrd) == 2: # id, password
            if id_psswrd[1] == data[1]:
                return id_psswrd[0] # returns id
            else :
                return  "INCORRECT PASSWORD" # incorrect password

        else:
            #username doesnt exist
            return "WHAT IN THE WORLD IS GOING ON"


    def user_login_details(self, data, type_of_login):
        user_name_or_mail_id , password = data[0] , data[1]

        if type_of_login == 'username':
            response = self.check_credentials((user_name_or_mail_id, password),'username')

        else:
            response = self.check_credentials((user_name_or_mail_id, password),'mail_id')

        return response


    def get_user_details(self, user_id):
        details = None
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT name, designation, emp_no, gender, branch_name, contact_no FROM USER_INFO WHERE id = %s", (user_id,))

        for i in self.cursor:
            details = i

        details = list(details)
        self.cursor.execute("SELECT check_in, check_out FROM USER_LOG WHERE id = %s", (user_id,))
        
        for i in self.cursor:
            details.append(i[0])

        self.cursor.close()
        
        return details      

    def update_log(self, user_id, check_in, check_out):
        # Input format : Date-Month-Year@Hour:Minute:Seconds
        # Required format : Year-Month-Date@Hour:Minute:Seconds

        self.cursor = self.db.cursor()  

        if check_in is not None:
            temp = check_in.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKIN TIME IS IN WRONG FORMAT"

            check_in = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            self.cursor.execute("INSERT INTO USER_LOG (id, check_in) VALUES (%s, %s)", (user_id,check_in))

        else :
            temp = check_out.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKOUT TIME IS IN WRONG FORMAT"

            check_out = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            self.cursor.execute("UPDATE USER_LOG set check_out = %s WHERE id = %s AND check_out IS NULL", (check_out, user_id))

        
        self.cursor.close()
        self.db.commit()  
            
        return "LOG UPDATED"

    def check_in_out(self, user_id):
        self.cursor = self.db.cursor()  
        self.cursor.execute("SELECT COUNT(*) FROM USER_LOG WHERE id = %s AND check_out IS NULL", (user_id,))
        results = None
        code = None
        #loop to print all the fetched details
        for r in self.cursor.fetchone():
            results = r

        if int(results) == 0:
            code = "CHECKED OUT"

        else :
            code = "CHECKED IN"

        self.cursor.close()

        return code


    def get_branch_info(self, branch_name):
        coords = []
        self.cursor = self.db.cursor()
        self.cursor.execute("SELECT latitude, longitude FROM GEO_LOCATION WHERE branch_name = %s", (branch_name,))

        for i in self.cursor:
            coords.append(i[0])

        self.cursor.close()

        if len(coords) == 0:
            return "INCORRECT BRANCH NAME"

        return {"latitude": coords[0], "longitude": coords[1]}


    def get_log_data(self, last_n_days):
        data = {'user_id':[], 'check_in':[], 'check_out':[]}

        self.cursor = self.db.cursor()
        self.cursor.execute("SELECT id, check_in, check_out  FROM USER_LOG WHERE DATE_PART('day', CURRENT_TIMESTAMP- check_in) <= %s;", (last_n_days,))

        for i in self.cursor:
            data['user_id'].append(i[0])
            data['check_in'].append(i[1])
            data['check_out'].append(i[2])

        self.cursor.close()

        return data

    def get_user_overview(self):
        args = "id name designation emp_no gender branch_name".split(" ")
        data = {i:[] for i in args}

        self.cursor = self.db.cursor()
        self.cursor.execute("SELECT id, name, designation, emp_no, gender, branch_name FROM USER_INFO;")

        for i in self.cursor:
            for arg_, value in zip(args, list(i)):
                 data[arg_].append(value)
                
        self.cursor.close()

        return data
        




