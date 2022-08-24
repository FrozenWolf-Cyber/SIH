

const DesignationFilter = document.querySelector('select[name="emp_designation"]');
const EmpNoFilter = document.querySelector('input[name="emp_no"]');

function fill_search_options()
{
   
    //collected all info
    let designation = [];
    emp_list1.forEach((emp) => {
        if(!designation.includes(emp.designation))
        {
            designation.push(emp.designation);
        }
    });
    // let emp_no_min = emp_list1[0].emp_no;
    // let emp_no_max = emp_list1[emp_list1.length-1].emp_no;


    //fill the info in the form
    designation.forEach((desig) => {
        DesignationFilter.innerHTML += `
        <option value="${desig}">${desig}</option>
        `;
    });

    // EmpNoFilter.max = emp_no_max;
    // EmpNoFilter.min = emp_no_min;

}
