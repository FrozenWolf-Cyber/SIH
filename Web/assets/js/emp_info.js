

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
//     let emp_logs = {}; 
//     for(let i = 0;i < attendance_log.user_id.length;i++)
//      {
//         if(attendance_log.user_id[i] == emp_id)
//         {
//             emp_logs[`${attendance_log.check_in[i].substr(0,10)}`] = {
//                check_in:attendance_log.check_in[i],
//                check_out:attendance_log.check_out[i]                  
//             };
//         }
//      }

//      console.log('Anand emp_log',emp_logs);
//      return emp_logs;
// }
// const monthNames = ["January", "February", "March", "April", "May", "June",
//   "July", "August", "September", "October", "November", "December"];
// class Calender{
//    constructor(logs)
//    {
//       this.attendance_log = logs; 
//       const now = new Date();
//       const mm = String(now.getMonth() + 1).padStart(2, '0');
//       const yyyy = now.getFullYear();
//       this.month = mm;
//       this.year = yyyy; 
//       this.DomElement = document.querySelector('#calender');
//       this.select_date;
//       this.CheckInElement = document.querySelector('#check-in');
//       this.CheckOutElement = document.querySelector('#check-out');
      
//       document.querySelector('#month-year').innerHTML = monthNames[(new Date(`${this.year}-${this.month}-01`)).getMonth()] + '-'+this.year;
//       this.DomElement.addEventListener('click',(event) => {
//          if(event.target.classList.contains('present'))
//          {
//             console.log('Anand');
//             console.log(this.CheckInElement,this.CheckOutElement);
//             this.CheckInElement = document.querySelector('#check-in');
//             this.CheckOutElement = document.querySelector('#check-out');
//             console.log(event.target.getAttribute('data-in').substr(11),event.target.getAttribute('data-out').substr(11));
//             this.CheckInElement.value = event.target.getAttribute('data-in').substr(11);
//             this.CheckOutElement.value = event.target.getAttribute('data-out').substr(11);
//             this.select_date = event.target;
//             for(let id = 1;id <= 37;id++)
//             {
//                 try{this.DomElement.querySelector('#d'+id).classList.remove('select');}catch{}
//             }
//             event.target.classList.add('select');
//          }
//          else if(event.target.classList.contains('dates'))
//          {
//             this.CheckInElement.value = '';
//             this.CheckOutElement.value = '';
//             this.select_date = event.target;
//             console.log('select',this.select_date);
//             for(let id = 1;id <= 37;id++)
//             {
//                 try{this.DomElement.querySelector('#d'+id).classList.remove('select');}catch{}
//             }
//             event.target.classList.add('select');
//          }
//          event.stopPropagation();
//       });
//    }
//    renderMonth()
//    {
//       console.log(this.year+'-'+this.month+'-01'); 
//       const first_date = new Date(this.year+'-'+this.month+'-01');
//       console.log(first_date);
//       const month = getAllDaysInMonth(first_date.getFullYear(), first_date.getMonth())
//       let start_date_no = first_date.getDay()+1;
//       let date = 1;

//       for(let id = 1;id <= 37;id++)
//       {
//          let DateElement = this.DomElement.querySelector(`#d${id}`);
//          DateElement.innerHTML = '';
//          try{DateElement.classList.remove('highlight')}catch{};
//          try{DateElement.classList.remove('present')}catch{};
//       }

//       for(let id = start_date_no;id <= month.length+start_date_no-1;id++)
//       {
//          let DateElement = this.DomElement.querySelector(`#d${id}`);
//          DateElement.innerHTML = ''+date;
//          DateElement.classList.add('highlight');
//          let date1 = date.toLocaleString('en-US', {
//             minimumIntegerDigits: 2,
//             useGrouping: false
//                                });
//          let emp_log = this.attendance_log[`${this.year}-${this.month}-${date1}`];
//          console.log(`${this.year}-${this.month}-${date1}`);
//          if(emp_log)
//          {
//              DateElement.setAttribute('data-in',emp_log.check_in);
//              DateElement.setAttribute('data-out',emp_log.check_out);
//              DateElement.classList.add('present'); 
//          }
//          date++;
//       }

//       this.CheckInElement.value = '';
//       this.CheckOutElement.value = '';
//       document.querySelector('#month-year').innerHTML = monthNames[(new Date(`${this.year}-${this.month}-01`)).getMonth()] + '-'+this.year;
//    }
//    incMonth()
//    {
//       let month = parseInt(this.month);
//       let year =  parseInt(this.year);
//       year += Math.floor(month/12);
//       month = month%12 + 1;
//       this.month = ''+month.toLocaleString('en-US', {
//                                                       minimumIntegerDigits: 2,
//                                                       useGrouping: false
//                                                                          });
//       this.year = ''+year.toLocaleString('en-US', {
//                                                     minimumIntegerDigits: 2,
//                                                     useGrouping: false
//                                                                          });
//       this.renderMonth();
//    }
//    decMonth()
//    {
//         let month = parseInt(this.month);
//         let year =  parseInt(this.year);
//         month = month - 1;
//         if(month == 0)
//         {
//             month = 12;
//             year = year-1;
//         }
//         this.month = ''+month.toLocaleString('en-US', {
//                                                         minimumIntegerDigits: 2,
//                                                         useGrouping: false
//                                                                         });
//         this.year = ''+year.toLocaleString('en-US', {
//                                                     minimumIntegerDigits: 2,
//                                                     useGrouping: false
//                                                                         });
//         this.renderMonth();
//    }
//    getDaysnum()
//    {
//       const first_date = new Date(this.year+'-'+this.month+'-01');
//       console.log(first_date);
//       const month = getAllDaysInMonth(first_date.getFullYear(), first_date.getMonth());
//       let total = 0,sun = 0;
//       month.forEach(date => {
//         if(date.getDay() == 0)
//         { 
//            sun++;
//         }
//         total++;
//       });

//       return {
//           total:total,
//           sundays:sun,
//           working:(total - sun)
//       };
//    }
// };

// const form = document.querySelector('form');
// let isUpdated = false;
// form.addEventListener('click',(event) => {
//     event.stopPropagation();
//     if(event.target.classList.contains('edit'))
//     {
//         event.preventDefault();
//         let CheckInElement = document.querySelector('#check-in');
//         let CheckOutElement = document.querySelector('#check-out');

//            let x = CheckInElement.value;
//            let y = CheckOutElement.value;
            
//            event.target.parentNode.querySelector('input').classList.add('editable-input');
//             if(!isUpdated)
//             {
//                 isUpdated = true;
//                 form.innerHTML += `
//                 <button id='update-button'>
//                     Update 
//                 </button>
//             `;
//                 form.querySelector('#update-button').addEventListener('click',(event) => {
//                     event.preventDefault();
//                     if(!calender.select_date.classList.contains('present'))
//                     {
//                         let data_check_out;
//                         if(document.querySelector('#check-out').value == '')
//                         {
//                             data_check_out = 'blah-null'
//                         }    
//                         else
//                         {
//                             data_check_out = calender.month + '.' + parseInt(calender.select_date.innerHTML).toLocaleString('en-US', {
//                                 minimumIntegerDigits: 2,
//                                 useGrouping: false
//                                                 })+ '.' + calender.year
// +'@'+ document.querySelector('#check-out').value
//                         }
//                         let data = JSON.parse(JSON.stringify({
//                             "user_id":'a'+emp_id +'a',
//                             "check_in":  calender.month + '.' + parseInt(calender.select_date.innerHTML).toLocaleString('en-US', {
//                                 minimumIntegerDigits: 2,
//                                 useGrouping: false
//                                                 })+ '.' + calender.year
// +'@'+ document.querySelector('#check-in').value,
//                             "check_out":data_check_out
//                         }));
//                         console.log(data);
//                         $.ajax('https://sih-smart-attendance.herokuapp.com/update_log', {
//                             type: 'POST',  
//                             data:data,  
//                             success: function (data, status, xhr) {
//                                 console.log(data);
//                                 document.querySelector('#check-in').value = '';
//                                 document.querySelector('#check-out').value ='';     
//                                 window.location.reload();   
//                             }
//                         });    
//                     }
                    
//                 })
//             }

//             document.querySelector('#check-in').value = x;
//             document.querySelector('#check-out').value = y;        
//     }
// });
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


// var emp_data;
// var calender;
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
//     emp_data = getEmpData();
//     console.log(emp_data)
//     calender = new Calender(emp_data);
//     calender.renderMonth();
// }

// function getAllDaysInMonth(year, month) {
//     const date = new Date(year, month, 1);
  
//     const dates = [];
  
//     while (date.getMonth() === month) {
//       dates.push(new Date(date));
//       date.setDate(date.getDate() + 1);
//     }
  
//     return dates;
//   }

// console.log(emp_data);


