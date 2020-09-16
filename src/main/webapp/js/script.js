var attributeCount = 1;
$(function() {
	//onchange propertyType
    $('#attributes').on('change', '.propertyType', function(){						     

        var propertytype = $(this).val();
        
    	if(propertytype == 2){
    		$(this).parents('.attributeRow').find('.typeConfig').html('<div class="row clearfix"><div class="col-sm-3"><div class="form-group"> <div class="form-line"><input type="text" class="form-control minRange" placeholder="  min range" ></div></div></div></div><div class="row clearfix"><div class="col-sm-6"></div> <div class="col-sm-3"><div class="form-group"> <div class="form-line"> <input type="text" class="form-control maxRange" placeholder="  max range" ></div></div></div></div>');
    	}
        if(propertytype == 1){
        	$(this).parents('.attributeRow').find('.typeConfig').html('');
    	}
        if(propertytype == 3){
        	$(this).parents('.attributeRow').find('.typeConfig').html(
        			'<div class="col-sm-2"><div class="form-group"> <div class="form-line">'+
           			' <input type="text" class="form-control enumelt" placeholder="Value" >  </div> </div> </div>	<div class="col-sm-1">	'+					
           			'<button type="button" style = "border: none;float: right;" class="btn btn-default btn-xs waves-effect" id="addenum">'+
           			'	<i style="color:blue" class="material-icons">add_circle</i>'+
        			'</button></div>');
    		}
        if(propertytype == 4){
        	$(this).parents('.attributeRow').find('.typeConfig').html('');
    		}
    	
      }); 
    
    //add attribute button
				    		  $('#attributes').on('click','#addAttribute', function(){
				    			  attributeCount++;
				    			  
				    			  $('#attributes').append('<div  id="attribute-' + attributeCount +'" class="attributeRow row clearfix">'+
		                                '<div  class="col-sm-3">'+
		                                   ' <div class="form-group">' +
		                                        '<div class="form-line">' +
		                                            '<input type="text" class="form-control propertyName" placeholder="property name" >' +
			                                   '</div>' +
		                                   ' </div>' +
		                               ' </div>' +
		                                '<div class="col-sm-3" >' +
		                                   ' <select class="form-control show-tick propertyType">' +
		                                        '<option value="">--Select a type--</option>' +
		                                        '<option value="1">Numeric</option>' +
		                                       ' <option value="2">Numeric range</option>' +
		                                       ' <option value="4">Literal</option>' +
		                                        '<option value="3">Enumeration</option>' +                             
		                                   ' </select>' +
		                                '</div> <div class="typeConfig enumerations"></div> ' +
		                              '</div> ');

				    			  $('#attribute-'+ attributeCount +' select:not(.ms)').selectpicker();
				    			});
	//add enumeration value
				    		  $('#attributes').on('click','#addenum',  function(){
				    			  console.debug(this);
				    			  $(this).parents('.attributeRow').find('.enumerations').append(
				    					  '<div class="col-sm-6"></div> <div  class=""><div class="col-sm-2"><div class="form-group"> <div class="form-line">'+
				    	           			' <input type="text" class="form-control enumelt" placeholder="Value" >  </div> </div> </div>');
				    			});
				    		  $('#super-attributes').on('click','#addenum',  function(){
				    			  console.debug(this);
				    			  $(this).parents('.superAttributeRow').find('.enumerations').append(
				    					  '<div class="col-sm-6"></div><div class="col-sm-1"></div>  <div  class="row clearfix"><div class="col-sm-3"><div class="form-group"> <div class="form-line">'+
				    	           			' <input type="text" class="form-control enumelt" placeholder="Value" >  </div> </div> </div>');
				    			});
				    		  
				    		  
				    		  
				    		  
})
  //return the type according to the id of the type
							function getType(id) {
				    			  if(id=="1")
				    				  return "Numeric";
				    			  else if (id=="2")
			    				  	 return "Numeric Range";
				    			  else if (id=="3")
			    				  	return "Enumeration";
				    			  else 
				    			    return "Literal";           
				    			}

							function getTypeId(type) {
								  if(type=="Numeric")
									  return "1";
								  else if (type=="Numeric Range")
								  	 return "2";
								  else if (type=="Enumeration")
								  	return "3";
								  else 
								    return "4";           
								}