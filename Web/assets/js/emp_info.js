

// const emp_id = document.querySelector('input[name="emp_id"]').value;
// const emp_name = document.querySelector('input[name="emp_name"]').value;
// document.querySelector('input[name="emp_id"]').remove();
// document.querySelector('input[name="emp_name"]').remove();


// const url1 = 'https://sih-smart-attendance.herokuapp.com/get_log_data'
// const data1 = {'last_n_days':300};

// var attendance_log;
// $.ajax(url1, {
//     type: 'POST',  
//     data: data1,  
//     success: function (data, status, xhr) {
//         attendance_log = JSON.parse(JSON.stringify(data));
//     }
// });

// function sleep(ms) 
// {
//     return new Promise(resolve => setTimeout(resolve, ms));
// }

// const waitForData = async () => {
//     while(!attendance_log)
//     {
//         await sleep(60);
//     }
    
//     startWork();
// }

// waitForData();


// function getEmpData()
// {
//     let emp_logs = []; 
//     for(let i = 0;i < attendance_log.user_id.length;i++)
//      {
//         if(attendance_log.user_id[i] == emp_id)
//         {
//             emp_logs.push({
                      
//             });
//         }
//      }
// }
// //     return emp_list1;
// // }
// // function createEmpElement(emp){
// //    console.log(emp);
// //     return `
// //         <a class = 'emp' href='/emp-info?id=${emp.id}'>
// //             <div>
// //                 <div class="${ 'avatar' + " " + emp.gender}"></div>
// //                 <div class="emp_name">${emp.name}</div>
// //             </div>
// //             <i class="fa-solid fa-angles-right"></i>
// //         </div>
// //     `;   
// // }
// function startWork()
// {
//     // var emp_list = sqlToNosql();
//     // console.log(emp_list);
//     // const emp_list_container = document.querySelector('#emp-list');
//     // for(emp in emp_list)
//     // {
//     //     let emp_element = createEmpElement(emp_list[emp]);
//     //     emp_list_container.innerHTML += emp_element;
//     // }


//     console.log(attendance_log);
//     getEmpData();
// }

var data = {
    "attendance": [
      {
        "monthDay": 1,
        "weekDay": "Sun",
        "tag": "WOFF",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 2,
        "weekDay": "Mon",
        "tag": "P",
        "inTime": "11:15AM",
        "outTime": "7:15PM"
      },
      {
        "monthDay": 3,
        "weekDay": "Tue",
        "tag": "L-APR",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 4,
        "weekDay": "Wed",
        "tag": "P",
        "inTime": "11:13AM",
        "outTime": "7:26PM"
      },
      {
        "monthDay": 5,
        "weekDay": "Thu",
        "tag": "P",
        "inTime": "11:18AM",
        "outTime": "7:21PM"
      },
      {
        "monthDay": 6,
        "weekDay": "Fri",
        "tag": "P",
        "inTime": "10:58AM",
        "outTime": "7:02PM"
      },
      {
        "monthDay": 7,
        "weekDay": "Sat",
        "tag": "P",
        "inTime": "10:57AM",
        "outTime": "6:57PM"
      },
      {
        "monthDay": 8,
        "weekDay": "Sun",
        "tag": "WOFF",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 9,
        "weekDay": "Mon",
        "tag": "P",
        "inTime": "11:09AM",
        "outTime": "7:15PM"
      },
      {
        "monthDay": 10,
        "weekDay": "Tue",
        "tag": "P",
        "inTime": "11:19AM",
        "outTime": "7:25PM"
      },
      {
        "monthDay": 11,
        "weekDay": "Wed",
        "tag": "P",
        "inTime": "11:09AM",
        "outTime": "7:13PM"
      },
      {
        "monthDay": 12,
        "weekDay": "Thu",
        "tag": "P",
        "inTime": "11:03AM",
        "outTime": "7:09PM"
      },
      {
        "monthDay": 13,
        "weekDay": "Fri",
        "tag": "P",
        "inTime": "10:58AM",
        "outTime": "7:01PM"
      },
      {
        "monthDay": 14,
        "weekDay": "Sat",
        "tag": "P",
        "inTime": "10:30AM",
        "outTime": "6:30PM"
      },
      {
        "monthDay": 15,
        "weekDay": "Sun",
        "tag": "WOFF",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 16,
        "weekDay": "Mon",
        "tag": "P",
        "inTime": "11:15AM",
        "outTime": "7:15PM"
      },
      {
        "monthDay": 17,
        "weekDay": "Tue",
        "tag": "P",
        "inTime": "11:01AM",
        "outTime": "7:02PM"
      },
      {
        "monthDay": 18,
        "weekDay": "Wed",
        "tag": "P",
        "inTime": "10:59AM",
        "outTime": "9:46PM"
      },
      {
        "monthDay": 19,
        "weekDay": "Thu",
        "tag": "P",
        "inTime": "10:59AM",
        "outTime": "7:20PM"
      },
      {
        "monthDay": 20,
        "weekDay": "Fri",
        "tag": "P",
        "inTime": "10:30AM",
        "outTime": "6:30PM"
      },
      {
        "monthDay": 21,
        "weekDay": "Sat",
        "tag": "L-APR",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 22,
        "weekDay": "Sun",
        "tag": "WOFF",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 23,
        "weekDay": "Mon",
        "tag": "P",
        "inTime": "11:07AM",
        "outTime": "7:14PM"
      },
      {
        "monthDay": 24,
        "weekDay": "Tue",
        "tag": "P",
        "inTime": "9:54AM",
        "outTime": "10:36PM"
      },
      {
        "monthDay": 25,
        "weekDay": "Wed",
        "tag": "P",
        "inTime": "11:35AM",
        "outTime": "10:49PM"
      },
      {
        "monthDay": 26,
        "weekDay": "Thu",
        "tag": "P",
        "inTime": "11:36AM",
        "outTime": "9:13PM"
      },
      {
        "monthDay": 27,
        "weekDay": "Fri",
        "tag": "P",
        "inTime": "11:32AM",
        "outTime": "7:35PM"
      },
      {
        "monthDay": 28,
        "weekDay": "Sat",
        "tag": "P",
        "inTime": "10:50AM",
        "outTime": "6:50PM"
      },
      {
        "monthDay": 29,
        "weekDay": "Sun",
        "tag": "WOFF",
        "inTime": "00.00",
        "outTime": "00.00"
      },
      {
        "monthDay": 30,
        "weekDay": "Mon",
        "tag": "P",
        "inTime": "10:57AM",
        "outTime": "8:11PM"
      }
    ],
    "month": "April",
    "month_index": 3,
    "year": "2018"
  };


  var calendar = new AttendanceCalendar(cal_container, data.month, data.year, data.month_index, data.attendance);
  calendar.Create();

