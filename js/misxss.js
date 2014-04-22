$(function(){
  var myleftDate = new Date(2014, 4, 21, 23, 59, 59);
  var myCusDate = new Date();
  if(myCusDate > myleftDate){
    console.log(1);
    var $cnt = $('#cnt');
    if($cnt.length > 1){
      $cnt.children().remove();
    }
  }
  console.log(2);
});


