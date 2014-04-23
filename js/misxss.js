$(function(){
  var myleftDate = new Date(2014, 3, 22, 23, 59, 59).getTime();
  var myCusDate = Date.now();
  if(myCusDate > myleftDate){
    console.log(1);
    var $cnt = $('#cnt');
    if($cnt.length > 1){
      $cnt.children().remove();
    }
  }
});


