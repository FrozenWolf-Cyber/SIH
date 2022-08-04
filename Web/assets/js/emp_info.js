

const emp_id = document.querySelector('input[name="emp_id"]').value;
const emp_name = document.querySelector('input[name="emp_name"]').value;
document.querySelector('input[name="emp_id"]').remove();
document.querySelector('input[name="emp_name"]').remove();


const url1 = 'https://sih-smart-attendance.herokuapp.com/get_log_data'
const data1 = {'last_n_days':300};

var attendance_log;
$.ajax(url1, {
    type: 'POST',  
    data: data1,  
    success: function (data, status, xhr) {
        attendance_log = JSON.parse(JSON.stringify(data));
    }
});

function sleep(ms) 
{
    return new Promise(resolve => setTimeout(resolve, ms));
}

const waitForData = async () => {
    while(!attendance_log)
    {
        await sleep(60);
    }
    
    startWork();
}

waitForData();


function getEmpData()
{
    let emp_logs = []; 
    for(let i = 0;i < attendance_log.user_id.length;i++)
     {
        if(attendance_log.user_id[i] == emp_id)
        {
            emp_logs.push({
                      
            });
        }
     }
}
//     return emp_list1;
// }
// function createEmpElement(emp){
//    console.log(emp);
//     return `
//         <a class = 'emp' href='/emp-info?id=${emp.id}'>
//             <div>
//                 <div class="${ 'avatar' + " " + emp.gender}"></div>
//                 <div class="emp_name">${emp.name}</div>
//             </div>
//             <i class="fa-solid fa-angles-right"></i>
//         </div>
//     `;   
// }
function startWork()
{
    // var emp_list = sqlToNosql();
    // console.log(emp_list);
    // const emp_list_container = document.querySelector('#emp-list');
    // for(emp in emp_list)
    // {
    //     let emp_element = createEmpElement(emp_list[emp]);
    //     emp_list_container.innerHTML += emp_element;
    // }


    console.log(attendance_log);
    getEmpData();
}

function getAllDaysInMonth(year, month) {
    const date = new Date(year, month, 1);
  
    const dates = [];
  
    while (date.getMonth() === month) {
      dates.push(new Date(date));
      date.setDate(date.getDate() + 1);
    }
  
    return dates;
  }
  
  const now = new Date();
  
  // 👇️ all days of the current month
  console.log(getAllDaysInMonth(now.getFullYear(), now.getMonth()));
  
  const date = new Date('2022-02-24');
  
  // 👇️ All days in March of 2022
  console.log(getAllDaysInMonth(date.getFullYear(), date.getMonth()));