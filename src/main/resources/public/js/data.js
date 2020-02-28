function executeQuery() {

    $.ajax('data?command=' + $('#dql').val()).done(function (response) {
          console.log(response);

        response = JSON.parse(response);
        if (response.headers) {
            var headersRow = $('#results > table > thead > tr');
            headersRow.empty();
            var tBody = $('#results > table > tbody');
            tBody.empty();
            response.headers.forEach(h => headersRow.append('<th scope="col">' + h + '</th>'));
            response.values.forEach(row => {
                var tr = $('<tr></tr>');
                row.forEach(value =>
                    tr.append('<td>' + value + '</td>')
                );
                tBody.append(tr);
            })
        }
    });

}

$('#btn-execute').click(executeQuery);

$('#dql').keydown(function (e) {

  if (e.ctrlKey && e.keyCode == 13) {
    executeQuery();
  }

});