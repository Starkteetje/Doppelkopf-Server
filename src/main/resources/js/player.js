google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {
  drawAverageRoundsChart();
  drawPlacementsChart();
  drawSignChart();
}

function drawAverageRoundsChart() {
  var dataForRoundsJson = JSON.parse(atob(document.getElementById('data').dataset.playerrounddata));
  if (dataForRoundsJson.length === 1) {
    dataForRoundsJson[1] = JSON.parse("[0,0]");
  }
  var ticks = document.getElementById('data').dataset.ticks;
  var dataForRounds = google.visualization.arrayToDataTable(dataForRoundsJson);

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


function drawPlacementsChart() {
  var dataForPlacementsJson = JSON.parse(atob(document.getElementById('data').dataset.playerplacementdata));
  var dataForPlacements = google.visualization.arrayToDataTable(dataForPlacementsJson);

  var optionsForPlacements = {
    title: 'Platzierung',
    slices: {
            0: { color: 'green' },
            1: { color: 'yellow' },
            2: { color: 'orange' },
            3: { color: 'red' }
          }
  };

  var chartForPlacements = new google.visualization.PieChart(document.getElementById('placements'));

  chartForPlacements.draw(dataForPlacements, optionsForPlacements);
}

function drawSignChart() {
  var dataForSignJson = JSON.parse(atob(document.getElementById('data').dataset.playersigndata));
  var dataForSign = google.visualization.arrayToDataTable(dataForSignJson);

  var optionsForSign = {
    title: 'Vorzeichen',
    slices: {
            0: { color: 'green' },
            1: { color: 'grey' },
            2: { color: 'red' }
          }
  };

  var chartForSign = new google.visualization.PieChart(document.getElementById('sign'));

  chartForSign.draw(dataForSign, optionsForSign);
}
