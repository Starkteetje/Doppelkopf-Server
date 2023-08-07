google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {
  if (document.getElementById('dataLatest')) {
    drawLatestAllSessionsChart();
    drawLatestPerSessionChart();
  }
  drawAllSessionsChart();
  drawPerSessionChart();
}

function drawAllSessionsChart() {
  var dataForAllSessionsJson = JSON.parse(atob(document.getElementById('data').dataset.alldata));
  var ticks = document.getElementById('data').dataset.ticks;
  var dataForAllSessions = google.visualization.arrayToDataTable( dataForAllSessionsJson );

  var optionsForAllSessions = {
    title: 'Ergebnisse aller Abende',
    legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartForAllSessions = new google.visualization.LineChart(document.getElementById('alleAbende'));

  chartForAllSessions.draw(dataForAllSessions, optionsForAllSessions);
}

function drawPerSessionChart() {
  var dataPerSessionJson = JSON.parse(atob(document.getElementById('data').dataset.perdata));
  var ticks = document.getElementById('data').dataset.ticks;
  var dataPerSession = google.visualization.arrayToDataTable( dataPerSessionJson );

  var optionsPerSession = {
    title: 'Ergebnisse des jeweiligen Abends',
    legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartPerSession = new google.visualization.LineChart(document.getElementById('proAbend'));

  chartPerSession.draw(dataPerSession, optionsPerSession);
}


function drawLatestAllSessionsChart() {
  var dataForAllSessionsJson = JSON.parse(atob(document.getElementById('dataLatest').dataset.alldata));
  var ticks = document.getElementById('dataLatest').dataset.ticks;
  var dataForAllSessions = google.visualization.arrayToDataTable( dataForAllSessionsJson );

  var optionsForAllSessions = {
    title: 'Ergebnisse aller Abende der aktuellen Saison',
    legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartForAllSessions = new google.visualization.LineChart(document.getElementById('alleAbendeLatest'));

  chartForAllSessions.draw(dataForAllSessions, optionsForAllSessions);
}

function drawLatestPerSessionChart() {
  var dataPerSessionJson = JSON.parse(atob(document.getElementById('dataLatest').dataset.perdata));
  var ticks = document.getElementById('dataLatest').dataset.ticks;
  var dataPerSession = google.visualization.arrayToDataTable( dataPerSessionJson );

  var optionsPerSession = {
    title: 'Ergebnisse des jeweiligen Abends der aktuellen Saison',
    legend: { position: 'bottom' },
	pointSize: 7,
	pointShape: 'square',
	hAxis: {ticks: ticks}
  };

  var chartPerSession = new google.visualization.LineChart(document.getElementById('proAbendLatest'));

  chartPerSession.draw(dataPerSession, optionsPerSession);
}