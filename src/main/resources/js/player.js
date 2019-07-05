google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {
  drawAverageRoundsChart();
}

function drawAverageRoundsChart() {
  var dataForRoundsJson = JSON.parse(atob(document.getElementById('data').dataset.playerrounddata));
  if (dataForRoundsJson.length === 1) {
    dataForRoundsJson[1] = JSON.parse("[0,0]");
  }
  var ticks = document.getElementById('data').dataset.ticks;
  var dataForRounds = google.visualization.arrayToDataTable( dataForRoundsJson );

  var optionsForRounds = {
    title: 'Durchschnittsergebnisse der Runden (min. 5 Abende)',
    legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartForAllSessions = new google.visualization.LineChart(document.getElementById('runden'));

  chartForAllSessions.draw(dataForRounds, optionsForRounds);
}