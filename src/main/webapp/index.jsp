<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js" ></script>
    <script language="javascript">
    var xmlhttp;
    function init() {
       // put more code here in case you are concerned about browsers that do not provide XMLHttpRequest object directly
       xmlhttp = new XMLHttpRequest();
    }
    function getdetails() {
    	
        var empno = $("#empno");

    	     
    	    $.ajax({
    	       url : 'http://localhost:8080/CEMConfigProject/cem/config/addProduct/' + empno.val(), // Le nom du script a changé, c'est send_mail.php maintenant !
    	       type : 'POST', 
    	     
    	    });
    	   

    	
    	
    	
       /* var url = "http://localhost:8080/CEMConfigProject/cem/config/addProduct/" + empno.value;
        xmlhttp.open('POST',url,true);
        xmlhttp.send(null);*/
        /*xmlhttp.onreadystatechange = function() {

               var empname =  document.getElementById("empname");
               var age =  document.getElementById("age");
               if (xmlhttp.readyState == 4) {
                  if ( xmlhttp.status == 200) {
                       var det = eval( "(" +  xmlhttp.responseText + ")");
                       if (det.age > 0 ) {
                          empname.value = det.name;
                          age.value = det.age;
                       }
                       else {
                           empname.value = "";
                           age.value ="";
                           alert("Invalid Employee ID");
                       }
                 }
                 else
                       alert("Error ->" + xmlhttp.responseText);
              }
        };*/
    }
  </script>
  </head>
  <body  onload="init()">
   <h1>Call Employee Service </h1>
   <table>
   <tr>
       <td>Enter Employee ID :  </td>
       <td><input type="text" id="empno" size="10"/>  <input type="button" value="Get Details" onclick="getdetails()"/>
   </tr>
   </table>
  </body>
</html>