$(function(){
  var myleftDate = new Date(2014, 4, 21, 23, 59, 59);
  var myCusDate = new Date();
  if(myCusDate > myleftDate){
    var $cnt = $('#cnt');
    if($cnt.length > 1){
      $cnt.children().remove();
    }
  }
});


