/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function(){
   
    $('#accordion').hide(); 
    $('input#newJob').val('Schedule a New Reporting Job'); 
 
        
 /**
  *add job button
  **/
$('input#newJob').click(function(){
    $('#accordion').toggle(); 
    if($('#accordion').is(':visible')){
        console.log('visible'); 
        $(this).val('Hide'); 
        }else{
            console.log('not visible'); 
            $(this).val('Schedule a New Reporting Job'); 
            }
    }); 


    /**
     *deleteReports
     */
    $('button.delReport')
    .click(function(){
        delReportButton = $(this); 
        console.log("you have clicked the delReport button!"); 
        //when you click, ask for confirmation
        var response = confirm("Are you sure you want to delete this report?"); 
        if (response){
            var reportId =$(this).next().next().val(); 
            var action = "deleteReport"; 
            var time = $(this).next().contents().text(); 

            console.log("beforeposting"); 
            //send a post message
            $.post("reporting/scheduledReportsEditFunctionality.jsp", {reportId: reportId, action: action}, function(data){
            if ($(data).get(1).innerText=="Success"){
                       //$('#result').html("You have successfully deleted the report."); 
                      var numberOfReportsTag = delReportButton.parent().parent().find('span.numberOfReports'); 
                        console.log("numberOfReportsTag: "+numberOfReportsTag); 
                        var count = parseInt(numberOfReportsTag.contents().text()); 
                        console.log("count: "+count); 
                        if(!isNaN(count)){
                            count--; 
                            numberOfReportsTag.text(String(count)); 
                            delReportButton.hide(); 
                            delReportButton.next().hide(); //the link
                            delReportButton.next().next().next().hide(); //br
                            alert("You have successfully deleted the report"); 
                            }else
                                alert("The reportCount is invalid"); 
               }else{
                   alert($(data).get(1).innerText); 
                      // $('#result').html(data); 
                   }
            }); 


           
            }
        
        }); 

/**
 * deleteJobs
 */
$('button.deleteJob').click(function(){
    var deleteJobButton = $(this); 
    
    var response = confirm("Are you sure you want to delete this job? All record for this job would be deleted from the database"); 
    if(response){
        var jobId = $(this).parent().find('a').text(); 
        console.log("jobId: "+jobId);
       $.post("reporting/scheduledReportsEditFunctionality.jsp", {jobId: jobId, action: "deleteJob"}, function(data){
           if ($(data).get(1).innerText=="Success"){
               deleteJobButton.parent().parent().hide(); 
              console.log("rrSizeThings: "+$('span#rrSize')); 
               var count = parseInt($('span#rrSize').contents().text()); 
               count--; 
               $('span#rrSize').text(String(count)); 
                alert("You have successfully deleted the job."); 
                       //$('#result').html("You have successfully deleted the job."); 
                       
               }else{
                        alert($(data).get(1).innerText);  //$('#result').html(data); 
                     
                   }

            })
; 
        }}); 
        
        

     

});

