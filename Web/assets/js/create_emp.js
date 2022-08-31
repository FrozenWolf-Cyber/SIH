

document.querySelector('button').addEventListener('click',(event) => {
   const data = $('form').serializeArray();
   document.body.style.opacity = '0.6';
   document.body.style.PointerEvent = 'none';
   $.ajax('http://sih-smart-attendance.herokuapp.com/admin_signup', {
    type: 'POST',
    data:data,  
    success: function (data, status, xhr) {
        document.body.style.opacity = '1'; 
        document.body.style.pointerEvents = 'inherit';
        new Noty({
          type:'success',
          theme:'mint',
          layout:'topRight',
          text: `Employee created Emp no:${data}`,
          timeout:1000
        }).show();
        console.log(data);
        let inputs = document.querySelectorAll('input');
        inputs.forEach((input) => {
          input.value = '';
        })
        //window.location.reload();
    }
    });
    event.stopPropagation();
    event.preventDefault();
});