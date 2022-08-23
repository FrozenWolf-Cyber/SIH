

const emp_no = document.querySelector('input[name="emp_no"]').value;
const male = document.querySelector('#male');
const female = document.querySelector('#female');
const GenderContainer = document.querySelector('#gender-container');
male.remove();
female.remove();
console.log(emp_no);var emp_data;
// url = 'http://127.0.0.1:5000/get_info'
// input data = {'emp_no' : emp_no}
$.ajax('http://sih-smart-attendance.herokuapp.com/get_info', {
        type: 'POST',  
        data:{emp_no:'a'+emp_no+'a'},
        success: function (data, status, xhr) {
             emp_data = data; 
             console.log(data);
             empProfileRender(data);
             davis(data);          
        }
    });
function empProfileRender(data){
    if(typeof data != 'string')
    {
        for(let field in data){
            let elements = document.querySelectorAll(`input[name="${field}"]`); 
            for(element of elements)
            {
                if( element && element.type == 'radio')
                {
                    console.log(element);
                    if(element.value == data.gender)
                    {
                        element.checked = true;
                    }
                }
                else if(element)
                {
                    element.value = data[field];
                }
            }
        };


        if(data.gender == 'M')
        {
          GenderContainer.innerHTML = '';
          GenderContainer.appendChild(male);  
        }
        else if(data.gender == 'F')
        {
          GenderContainer.innerHTML = '';
          GenderContainer.appendChild(female);
        }
        else
        {
            GenderContainer.innerHTML = '';
        }

    }
    else
    {
        let inputs = document.querySelectorAll('input');
        inputs.forEach((input) => {
            input.value = '';
        });
    }
};
// $.ajax('http://sih-smart-attendance.herokuapp.com/get_img', {
//         type: 'POST',  
//         data:{emp_no:'a'+emp_no+'a'},
//         success: function (data, status, xhr) {
//              console.log('get_image',data);    
//              // strip off the data: url prefix to get just the base64-encoded bytes
//             //  $.ajax('/profile_img',{
//             //     type:'POST',
//             //     data,
//             //     success:function (data,status,xhr){
//             //         console.log(data);
//             //     }
//             //  });
             
//         }
// });



// url = 'http://127.0.0.1:5000/get_img'

//     input data = requests.post(url, data= {'emp_no' : emp_no})
//     img = io.BytesIO(x.content)
//     a = Image.open(img)
//     print(a.size)


const intervalId = setInterval(() => {
    
    if(emp_data){
        console.log('not: fetched!'); 
        clearInterval(intervalId);
          
    }
    else{
       console.log('not:fetching data...');
    }

},600);