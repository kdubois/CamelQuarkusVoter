
var barColors = ["red", "green","blue","orange","brown"];
var ctx_live = document.getElementById("myChart");

var myChart = new Chart(ctx_live, {
        type: "bar",
        data: {
          labels: [],
          datasets: [{
            label: 'Fav IDE',
            backgroundColor: barColors,
            data: []
          }]
        },
        options: {
          responsive: true,
          animation: false
        }
      });     
    
function buttonClick(shortname){
  
        console.log( "vote cast for " + shortname);

        $.post({
          url: ingester_url+"/favstackxform",
          data: {"shortname": shortname},
          type: "POST",
          crossDomain: true,
          dataType: "json",
        });
    
}

var getData = function () {
  $.getJSON( processor_url+"/getresults", { format: "jsonp" }, 
    function(data){
      
      var xValues = [];
      var yValues = [];
      var i = 1;
      myChart.data.labels = [];
      myChart.data.datasets[0].data = [];
      $(data).each(
        function(){
          $("#"+this.shortname+" .position").html(i);
          $("#"+this.shortname+" .fullname").html(this.fullname);
          $("#"+this.shortname+" .counter").html(this.counter);            
           
          myChart.data.labels.push(this.fullname);
          myChart.data.datasets[0].data.push(this.counter);
          myChart.update();
          i++;
        }
      );            
    }
  );  
}

getData;

setInterval(getData, 2000);