import databases
import sqlalchemy
import random
import string


class Database:
    def __init__(self,host,user,passwd,database):
        DATABASE_URL = f'postgresql+psycopg2://{user}:{passwd}@{host}:5432/{database}'
 
        self.database = databases.Database(DATABASE_URL)
        self.metadata = sqlalchemy.MetaData()

        self.engine = sqlalchemy.create_engine(DATABASE_URL)
        self.metadata.create_all(self.engine)


        self.loc_database = [('Chennai', '13.0827', '80.2707'),
                           ('Mumbai', '19.0760', '72.8777'),
                           ('Delhi', '28.7041', '77.1025'),
                           ('Kolkata ', '22.5726', '88.3639'),
                          ]

    
    async def create(self):
        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_INFO (
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

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_LOGIN ( 
                          mail_id VARCHAR(100) UNIQUE ,
                          user_name VARCHAR(100) UNIQUE ,
                          password VARCHAR(100) NOT NULL ,
                          id VARCHAR(20) UNIQUE ,
                          PRIMARY KEY (id));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_LOG ( 
                          id VARCHAR(20)  ,
                          check_in TIMESTAMP,
                          check_out TIMESTAMP);
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS GEO_LOCATION ( 
                          branch_name VARCHAR(100)  ,
                          latitude VARCHAR(20),
                          longitude VARCHAR(20));
        ''')

        await self.database.execute('''CREATE TABLE IF NOT EXISTS USER_IMG ( 
                          id VARCHAR(20)  ,
                          data BYTEA,
                          PRIMARY KEY (id));
        ''')


    def generate_user_id(self): # unique id varies from 8-15 characters
        unique_id = ''
        for i in range(random.randrange(8,15)): 
            unique_id = unique_id + random.choice(string.printable[:61])

        return unique_id

    async def check_user_id_exist(self, user_id):
        exist = False
        r = await self.database.execute(f"SELECT id FROM USER_LOGIN WHERE id = '{user_id}'")
        if r is None:
            return False

        else: 
            return True


    async def check_unique_data(self, data):
        check = True
        # d = await self.database.execute()
        for i in await self.database.fetch_all(f"SELECT mail_id , user_name FROM USER_LOGIN WHERE user_name = '{data[1]}'"):
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
        user_name, password, mail_id, name, designation, emp_no, gender, user_id, branch_name, contact_no, embed1, embed2, embed3 = data
        await self.database.execute("INSERT INTO USER_LOGIN (mail_id , user_name , password , id) VALUES ('%s', '%s', '%s', '%s')" % (mail_id, user_name, password, user_id))
        await self.database.execute("INSERT INTO USER_INFO (id , name , designation, emp_no , gender, branch_name , contact_no , embed1, embed2, embed3) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '{%s}', '{%s}', '{%s}')" % (user_id, name, designation, emp_no, gender, branch_name, contact_no, ', '.join(embed1), ', '.join(embed2), ', '.join(embed3)))

        return 1


    async def get_embeds(self, user_id):
        
        for i in await self.database.fetch_all("SELECT embed1, embed2, embed3 FROM USER_INFO WHERE id = '%s'"% (user_id,)):
            details = list(i.values())

        return details


    async def update_db(self, data):
        user_name, password, mail_id, name, designation, emp_no, gender, user_id, branch_name, contact_no = data
        await self.database.execute("UPDATE USER_LOGIN set mail_id = '%s', user_name = '%s', password = '%s' WHERE id = '%s'" % (mail_id, user_name, password, user_id))
        await self.database.execute("UPDATE USER_INFO set name = '%s', designation = '%s', emp_no = '%s', gender = '%s', branch_name = '%s', contact_no = '%s' WHERE id = '%s'" % (name, designation, emp_no, gender, branch_name, contact_no, user_id))
        return 1


    async def upload_img(self, user_id, file):
        # print(psycopg2.Binary(file), flush=True)
        # print('\n\n========================================================\n\n',file, flush=True)
        await self.database.execute("INSERT INTO USER_IMG (id, data) VALUES ('%s', %s::bytea)" % (user_id, str(file)[1:]))


    async def get_img(self, user_id):
        data = await self.database.fetch_all("SELECT data FROM USER_IMG WHERE id = '%s'" % (user_id,))
        return tuple(data[0].values())[0]


    async def sign_up(self, data):
        mail_id, user_name, password, name, designation, emp_no, gender, branch_name, contact_no, embed1, embed2, embed3 = data
        user_name_availablity = await self.check_unique_data((mail_id, user_name))
        unique_id = None
        if user_name_availablity:
            while True:
                unique_id = self.generate_user_id()
                if await self.check_user_id_exist(unique_id):
                    continue
                
                else :
                    await self.add_db((user_name, password, mail_id, name, designation, emp_no, gender, unique_id, branch_name, contact_no, embed1, embed2, embed3))
                    break

        return  user_name_availablity, unique_id

    
    async def check_credentials(self, data, user_name_or_mail_id): # data = [username, password]
        id_psswrd = None
        pswrd_incorrect = 0

        if user_name_or_mail_id == 'username' :
            for i in await self.database.fetch_all("SELECT id , password FROM USER_LOGIN WHERE user_name = '%s' " % (data[0], ) ):
                i = tuple(i.values())
                id_psswrd = i

        elif user_name_or_mail_id == 'mail_id' :
            for i in await self.database.fetch_all("SELECT id , password  FROM USER_LOGIN WHERE mail_id = '%s' " % (data[0],) ):
                i = tuple(i.values())
                id_psswrd = i

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


    async def user_login_details(self, data, type_of_login):
        user_name_or_mail_id , password = data[0] , data[1]

        if type_of_login == 'username':
            response = await self.check_credentials((user_name_or_mail_id, password),'username')

        else:
            response = await self.check_credentials((user_name_or_mail_id, password),'mail_id')

        return response


    async def get_user_details(self, user_id):
        details = None

        for i in await self.database.fetch_all("SELECT name, designation, emp_no, gender, branch_name, contact_no FROM USER_INFO WHERE id = '%s'" % (user_id,)):
            i = tuple(i.values())
            details = i

        details = list(details)
        
        for i in await self.database.fetch_all("SELECT check_in, check_out FROM USER_LOG WHERE id = '%s'" % (user_id,)):
            i = tuple(i.values())
            details.append(i[0])
        
        return details      


    async def update_log(self, user_id, check_in, check_out):
        # Input format : Date-Month-Year@Hour:Minute:Seconds
        # Required format : Year-Month-Date@Hour:Minute:Seconds

        if check_in is not None:
            temp = check_in.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKIN TIME IS IN WRONG FORMAT"

            check_in = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            await self.database.execute("INSERT INTO USER_LOG (id, check_in) VALUES ('%s', '%s')" % (user_id,check_in))

        else :
            temp = check_out.split('@')
            if len(temp)!=2:
                return "GIVEN CHECKOUT TIME IS IN WRONG FORMAT"

            check_out = '-'.join(temp[0].split('-')[::-1]) + '@' + temp[1]
            await self.database.execute("UPDATE USER_LOG set check_out = '%s' WHERE id = '%s' AND check_out IS NULL" % (check_out, user_id))
            
        return "LOG UPDATED"


    async def check_in_out(self, user_id):
        results = await self.database.fetch_one("SELECT COUNT(*) FROM USER_LOG WHERE id = '%s' AND check_out IS NULL" % (user_id,))
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
            coords.append(i[0])

        if len(coords) == 0:
            return "INCORRECT BRANCH NAME"

        return {"latitude": coords[0], "longitude": coords[1]}


    async def get_log_data(self, last_n_days):
        data = {'user_id':[], 'check_in':[], 'check_out':[]}

        for i in await self.database.fetch_all("SELECT id, check_in, check_out  FROM USER_LOG WHERE DATE_PART('day', CURRENT_TIMESTAMP- check_in) <= %s;" % (last_n_days,)):
            i = tuple(i.values())
            data['user_id'].append(i[0])
            data['check_in'].append(i[1])
            data['check_out'].append(i[2])

        return data


    async def get_user_overview(self):
        args = "id name designation emp_no gender branch_name".split(" ")
        data = {i:[] for i in args}

        for i in await self.database.fetch_all("SELECT id, name, designation, emp_no, gender, branch_name FROM USER_INFO;"):
            i = list(i.values())
            for arg_, value in zip(args, i):
                 data[arg_].append(value)
                
        return data
        
