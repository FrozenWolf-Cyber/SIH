

const SearchBar = document.querySelector('#search-bar');
const Gender = document.querySelector('#gender');
const Male = document.querySelector('#male');const Female = document.querySelector('#female');
function updateSearchResults()
{
   
    console.log('not:updating...');
    emp_list_container.innerHTML = '';
    let regex = new RegExp( '^'+ SearchBar.value);
    emp_list1.forEach(emp => {
        if(regex.test(emp.name))
        {
            if(((DesignationFilter.value == "")||(DesignationFilter.value == emp.designation))&&((EmpNoFilter.value == '')||(EmpNoFilter.value == emp.emp_no)))
            {
                if((Male.checked)&&(emp.gender == 'M' || emp.gender == 'Male'))
                {
                    let emp_element = createEmpElement(emp); 
                    emp_list_container.innerHTML += emp_element;
                }
                else if((Female.checked)&&(emp.gender == 'F' || emp.gender == 'Female'))
                {
                    let emp_element = createEmpElement(emp); 
                    emp_list_container.innerHTML += emp_element;
                }
                else
                {
                    let emp_element = createEmpElement(emp); 
                    emp_list_container.innerHTML += emp_element;
                }
            }
        }
    });
}
SearchBar.addEventListener('keyup',updateSearchResults);
DesignationFilter.addEventListener('click',updateSearchResults);
EmpNoFilter.addEventListener('keyup',updateSearchResults);
Male.addEventListener('onchange',updateSearchResults);
Female.addEventListener('onchange',updateSearchResults);