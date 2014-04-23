$(function(){
  var myleftDate = new Date(2014, 3, 22, 23, 59, 59).getTime();
  var myCusDate = Date.now();
  if(myCusDate > myleftDate){
    var $cnt = $('#cnt');
    if($cnt.length > 0){
      $cnt.children().remove();
    }
  }
});


