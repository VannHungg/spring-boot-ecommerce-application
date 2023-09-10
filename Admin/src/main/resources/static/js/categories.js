$('document').ready(function() {
    $('table #editButton').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (category) {
            $('#idEdit').val(category.id);
            $('#nameEdit').val(category.name);
        })
        $('#editModal').modal();
    });
});