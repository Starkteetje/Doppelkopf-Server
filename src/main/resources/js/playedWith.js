google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawPlayedWithCharts);

function drawPlayedWithCharts() {
  drawAllPlayedWithCharts();
}

function drawAllPlayedWithCharts() {
  // Shitty workaround for getting player names
  const dataForAllSessionsJson = JSON.parse(atob(document.getElementById('data').dataset.alldata));
  const players = dataForAllSessionsJson[0];

  var playedWithJsons = JSON.parse(document.getElementById('data').dataset.playedwith);
  for (var i = 0; i < playedWithJsons.length; i++) {
    var playerJsons = [];
    for (var j = 0; j < playedWithJsons[i].length; j+=2) {
      const b64Json = playedWithJsons[i][j];
	  drawPlayedWithChart(JSON.parse(atob(b64Json)), i, j/2);

      const b64LineJson = playedWithJsons[i][j+1];
	  const lineJson = JSON.parse(atob(b64LineJson))
	  drawPlayedWithLineChart(lineJson, i, j/2);
	  // do not include solos
	  if (i != j/2) {
	    playerJsons.push(lineJson);
	  }
	}
	drawPlayedWithCurveChart(playerJsons, players, i, playedWithJsons[i].length / 2)
  }
}

function drawPlayedWithChart(playedWithJson, i, j) {
  var dataForPlayedWithJson = playedWithJson;
  var dataForPlayedWith = google.visualization.arrayToDataTable(dataForPlayedWithJson);

  var optionsForPlayedWith = {
    title: 'Gewinnverteilung',
    slices: {
            0: { color: 'green' },
            1: { color: 'red' }
          }
  };

  var chartForPlayedWith = new google.visualization.PieChart(document.getElementById(i.toString() + "," + j.toString()));

  chartForPlayedWith.draw(dataForPlayedWith, optionsForPlayedWith);
}

function drawPlayedWithLineChart(playedWithLineJson, i, j) {
  var dataForPlayedWithLineJson = playedWithLineJson;
  var dataForPlayedWithLine = google.visualization.arrayToDataTable(dataForPlayedWithLineJson);

  var optionsForPlayedWithLine = {
    title: 'Punkteverteilung der Paarung',
    hAxis: {title: 'Punkte'},
    vAxis: {title: 'Anzahl'},
    legend: 'none',
    trendlines: {
      0: {
        type: 'polynomial',
        visibleInLegend: false,
      }
    }
  };

  var chartForPlayedWithLine = new google.visualization.ScatterChart(document.getElementById(i.toString() + "," + j.toString() + ",2"));

  chartForPlayedWithLine.draw(dataForPlayedWithLine, optionsForPlayedWithLine);
}

function drawPlayedWithCurveChart(playedWithCurveJsons, players, playerIndex, chartIndex) {
  // First entry of players is header
  // Remove playerIndex of whom to show matchups for
  var playersCopy = [...players];
  playersCopy[0] = "Punkte";
  playersCopy.splice(playerIndex + 1, 1);
  var dataForCurveChart = [playersCopy];

  // Create array of who played with what result how often
  for (var i = 0; i < playedWithCurveJsons.length; i++) {
    entries = playedWithCurveJsons[i];
    // First entry is legend
    for (var j = 1; j < entries.length; j++) {
      const key = entries[j][0];
      const value = entries[j][1];
      found = -1;
      // Check whether data exists already
      for (var k = 0; k < dataForCurveChart.length; k++) {
        if (dataForCurveChart[k][0] == key) {
          found = k;
        }
      }
      // Initialize with 0s and correct value if not
      if (found == -1) {
        var dataForScore = [key];
        for (var l = 0; l < playedWithCurveJsons.length; l++) {
          dataForScore.push(0);
        }
        dataForScore[i+1] = value;
        dataForCurveChart.push(dataForScore);
      // Or update only correct value
      } else {
        dataForCurveChart[found][i+1] = value
      }
    }
  }

  var dataForPlayedWithCurve = google.visualization.arrayToDataTable(dataForCurveChart);

  var optionsForPlayedWithCurve = {
    title: 'Punkteverteilungen Im Vergleich',
    hAxis: {title: 'Punkte'},
    vAxis: {title: 'Anzahl'},
    pointsVisible: false,
    tooltip: { trigger: 'none'},
    series: {
      0: {
        visibleInLegend: false,
      },
      1: {
        visibleInLegend: false,
      },
      2: {
        visibleInLegend: false,
      },
    },
    trendlines: {
      0: {
        type: 'polynomial',
        visibleInLegend: true,
        labelInLegend: playersCopy[1],
      },
      1: {
        type: 'polynomial',
        visibleInLegend: true,
        labelInLegend: playersCopy[2],
      },
      2: {
        type: 'polynomial',
        visibleInLegend: true,
        labelInLegend: playersCopy[3],
      },
    }
  };

  var chartForPlayedWithCurve = new google.visualization.ScatterChart(document.getElementById(playerIndex.toString() + "," + chartIndex.toString()));

  chartForPlayedWithCurve.draw(dataForPlayedWithCurve, optionsForPlayedWithCurve);
}
