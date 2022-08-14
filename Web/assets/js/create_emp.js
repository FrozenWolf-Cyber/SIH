

document.querySelector('button').addEventListener('click',(event) => {
   const data = $('form').serializeArray();
   $.ajax('http://sih-smart-attendance.herokuapp.com/admin_signup', {
    type: 'POST',
    data:data,  
    success: function (data, status, xhr) {
         console.log(data);
    }
    });
    event.stopPropagation();
    event.preventDefault();
});