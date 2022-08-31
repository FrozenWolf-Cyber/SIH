

const SearchBar = document.querySelector('#search-bar');
const Gender = document.querySelector('#gender');

function updateSearchResults()
{
   
    console.log('not:updating...');
    emp_list_container.innerHTML = '';
    let regex = new RegExp( '^'+ SearchBar.value);
    emp_list1.forEach(async (emp) => {
        if(regex.test(emp.name))
        {
            if(((DesignationFilter.value == "")||(DesignationFilter.value == emp.designation))&&((EmpNoFilter.value == '')||(EmpNoFilter.value == emp.emp_no)))
            {
                // if(Gender.value == 'M')
                // {
                //     let emp_element = createEmpElement(emp); 
                //     emp_list_container.innerHTML += emp_element;
                // }
                // else if(Gender.value = 'F')
                // {
                //     let emp_element = createEmpElement(emp); 
                //     emp_list_container.innerHTML += emp_element;
                // }
                // else
                // {
                //     let emp_element = createEmpElement(emp); 
                //     emp_list_container.innerHTML += emp_element;
                // }
                if(Gender.value == '' || Gender.value == emp.gender){
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
Gender.addEventListener('click',updateSearchResults);