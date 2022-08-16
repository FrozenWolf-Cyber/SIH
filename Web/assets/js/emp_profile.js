

const emp_no = document.querySelector('input[name="emp_no"]').value;
console.log(emp_no);var emp_data;
// url = 'http://127.0.0.1:5000/get_info'
// input data = {'emp_no' : emp_no}
$.ajax('http://sih-smart-attendance.herokuapp.com/get_info', {
        type: 'POST',  
        data:{emp_no:'a'+emp_no+'a'},
        success: function (data, status, xhr) {
             emp_data = data; 
             empProfileRender(data);
             davis(data);          
        }
    });

function empProfileRender(data){
    if(typeof data != 'string')
    {
        for(let field in data){
            let element = document.querySelector(`input[name="${field}"]`); 
            if( element && element.type == 'radio')
            {
                if(element.value == data.gender)
                {
                    element.checked = true;
                }
            }
            else if(element)
            {
                element.value = data[field];
            }
        };

    }
    else
    {
        let inputs = document.querySelectorAll('input');
        inputs.forEach((input) => {
            input.value = '';
        });
    }
};
// const intervalId = setInterval(() => {
    
//     if(emp_data){
//         console.log('not: fetched!'); 
//         clearInterval(intervalId);
          
//     }
//     else{
//        console.log('not:fetching data...');
//     }

// },600);