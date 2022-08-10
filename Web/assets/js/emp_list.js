

console.log('Anand');
var emp_list;
$.ajax('https://sih-smart-attendance.herokuapp.com/get_user_overview', {
    type: 'POST',  
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
        console.log('fetching data ...');
        await sleep(60);
    }
    console.log('emp_list',emp_list);
    startWork();
}
waitForData();
function sqlToNosql(){
    var emp_list1 = [];
    
    for(let i = 0;i < (emp_list.emp_no.length);i++)
    {
        emp_list1[i] = {
            name:emp_list.name[i],
            gender:emp_list.gender[i],
            emp_no:emp_list.emp_no[i],
            designation:emp_list.designation[i],
            branch_name:emp_list.branch_name[i]
        }
    }

    console.log(emp_list1)
    return emp_list1;
}
function createEmpElement(emp){
   let emp_details = JSON.stringify(emp)
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
var emp_list1;
function startWork()
{
    emp_list1 = sqlToNosql();
    const emp_list_container = document.querySelector('#emp-list');
    for(emp of emp_list1)
    {
        let emp_element = createEmpElement(emp);
        emp_list_container.innerHTML += emp_element;
    }
}