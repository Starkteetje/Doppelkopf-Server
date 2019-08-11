google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawPlayerCharts);

function drawPlayerCharts() {
  drawPlacementsCharts();
  drawSignCharts();
}

function drawPlacementsCharts() {
  var placementJson0 = JSON.parse(atob(document.getElementById('data').dataset.playerplacementdata0));
  var placementJson1 = JSON.parse(atob(document.getElementById('data').dataset.playerplacementdata1));
  var placementJson2 = JSON.parse(atob(document.getElementById('data').dataset.playerplacementdata2));
  var placementJson3 = JSON.parse(atob(document.getElementById('data').dataset.playerplacementdata3));

  drawPlacementsChart(placementJson0, 0);
  drawPlacementsChart(placementJson1, 1);
  drawPlacementsChart(placementJson2, 2);
  drawPlacementsChart(placementJson3, 3);
}

function drawPlacementsChart(placementJson, i) {
  var dataForPlacementsJson = placementJson;
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

  var chartForPlacements = new google.visualization.PieChart(document.getElementById('placements' + i.toString()));

  chartForPlacements.draw(dataForPlacements, optionsForPlacements);
}

function drawSignCharts() {
  var signJson0 = JSON.parse(atob(document.getElementById('data').dataset.playersigndata0));
  var signJson1 = JSON.parse(atob(document.getElementById('data').dataset.playersigndata1));
  var signJson2 = JSON.parse(atob(document.getElementById('data').dataset.playersigndata2));
  var signJson3 = JSON.parse(atob(document.getElementById('data').dataset.playersigndata3));

  drawSignChart(signJson0, 0);
  drawSignChart(signJson1, 1);
  drawSignChart(signJson2, 2);
  drawSignChart(signJson3, 3);
}

function drawSignChart(signJson, i) {
  var dataForSignJson = signJson;
  var dataForSign = google.visualization.arrayToDataTable(dataForSignJson);

  var optionsForSign = {
    title: 'Vorzeichen',
    slices: {
            0: { color: 'green' },
            1: { color: 'grey' },
            2: { color: 'red' }
          }
  };

  var chartForSign = new google.visualization.PieChart(document.getElementById('sign' + i.toString()));

  chartForSign.draw(dataForSign, optionsForSign);
}
