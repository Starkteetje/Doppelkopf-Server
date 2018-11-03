google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  var dataForAllRoundsJson = JSON.parse(atob(document.getElementById('data').dataset.rounddata));
  var ticks = document.getElementById('data').dataset.ticks;
  var dataForAllRounds = google.visualization.arrayToDataTable( dataForAllRoundsJson );

  var optionsForAllRounds = {
	title: 'Punkteverlauf des Abends',
	legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartForAllRounds = new google.visualization.LineChart(document.getElementById('verlauf'));

  chartForAllRounds.draw(dataForAllRounds, optionsForAllRounds);
  }