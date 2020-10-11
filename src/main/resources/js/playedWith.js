google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawPlayedWithCharts);

function drawPlayedWithCharts() {
  drawAllPlayedWithCharts();
}

function drawAllPlayedWithCharts() {
  var playedWithJsons = JSON.parse(document.getElementById('data').dataset.playedwith);
  for (var i = 0; i < playedWithJsons.length; i++) {
    for (var j = 0; j < playedWithJsons[i].length; j++) {
    var b64json = playedWithJsons[i][j];
	  drawPlayedWithChart(JSON.parse(atob(b64json)), i, j);
	}
  }
}

function drawPlayedWithChart(playedWithJson, i, j) {
  var dataForPlayedWithJson = playedWithJson;
  var dataForPlayedWith = google.visualization.arrayToDataTable(dataForPlayedWithJson);

  var optionsForPlayedWith = {
    title: 'Gewinn/Verlustverteilung',
    slices: {
            0: { color: 'green' },
            1: { color: 'red' }
          }
  };

  var chartForPlayedWith = new google.visualization.PieChart(document.getElementById(i.toString() + "," + j.toString()));

  chartForPlayedWith.draw(dataForPlayedWith, optionsForPlayedWith);
}
