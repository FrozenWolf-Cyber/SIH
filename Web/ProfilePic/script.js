

$.ajax('http://sih-smart-attendance.herokuapp.com/get_img', {
    type: 'POST',  
    data:{emp_no:'a'+1+'a'},
    success: function (data, status, xhr) {
        const img  = document.querySelector('#profile');
        // function str2ab(str) {
        //     var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
        //     var bufView = new Uint16Array(buf);
        //     for (var i=0, strLen=str.length; i < strLen; i++) {
        //     bufView[i] = str.charCodeAt(i);
        //     }
        //     return buf;
        // }
        function str2ab(str) {
            var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
            var bufView = new Uint16Array(buf);
            for (var i=0, strLen=str.length; i < strLen; i++) {
            bufView[i] = str.charCodeAt(i);
            }
            return buf;
        }
        // const utf8Encode = new TextEncoder();
        // const byteArr = utf8Encode.encode(data);
        function arrayBufferToBase64( buffer ) {
            var binary = '';
            var bytes = new Uint8Array( buffer );
            var len = bytes.byteLength;
            for (var i = 0; i < len; i++) {
                binary += String.fromCharCode( bytes[ i ] );
            }
            return window.btoa( binary );
        }
        // const base64 = arrayBufferToBase64(str2ab(data));        
        // console.log(byteArr.length);
        const blob = new Blob(str2ab(data), { type: "image/jpeg" });
        const imageUrl = URL.createObjectURL(blob);
        document.getElementById("profile").src = "data:image/jpg;base64," + base64;

    }
});