

const url1 = 'https://sih-smart-attendance.herokuapp.com/get_log_data'
const data1 = {'last_n_days':300};

const url2 = 'https://sih-smart-attendance.herokuapp.com/get_user_overview'


var attendance_log;
$.ajax(url1, {
    type: 'POST',  
    data: data1,  
    success: function (data, status, xhr) {
        attendance_log = JSON.parse(JSON.stringify(data));
    }
});

var emp_list;
$.ajax(url2, {
    type: 'POST',  // http method
    success: function (data, status, xhr) {
        emp_list = JSON.parse(JSON.stringify(data));
    }
});


function sleep(ms) 
{
    return new Promise(resolve => setTimeout(resolve, ms));
}


const waitForData = async () => {
    while(!(emp_list))
    {
        await sleep(60);
    }
    
    startWork();
}

waitForData();
function sqlToNosql(){
    var emp_list1 = {};
    
    for(let i = 0;i < emp_list.id.length;i++)
    {
        emp_list1[i] = {
            id:emp_list.id[i],
            name:emp_list.name[i],
            gender:emp_list.gender[i],
            emp_no:emp_list.emp_no[i],
            designation:emp_list.designation[i],
            branch_name:emp_list.branch_name[i]
        }
    }

    return emp_list1;
}
function createEmpElement(emp){
   console.log(emp);
   let emp_details = JSON.stringify(emp)
   console.log('emp_details',emp_details);
    return `
        <a class = 'emp' href='/emp-info?emp_details=${emp_details}'>
            <div>
                <div class="${ 'avatar' + " " + emp.gender}"></div>
                <div class="emp_name">${emp.name}</div>
            </div>
            <i class="fa-solid fa-angles-right"></i>
        </div>
    `;   
}
function startWork()
{
    var emp_list = sqlToNosql();
    console.log(emp_list);
    const emp_list_container = document.querySelector('#emp-list');
    for(emp in emp_list)
    {
        let emp_element = createEmpElement(emp_list[emp]);
        emp_list_container.innerHTML += emp_element;
    }
}

