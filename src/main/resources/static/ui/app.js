const Keyboard = window.SimpleKeyboard.default;

const myKeyboard = new Keyboard({
  onKeyPress: button => onKeyPress(button),
  layout:  { 'default': [
              'й ц у к е н г ш щ з х',
              'ф і ї в а п р о л д ж є',
              'я ч с м и т ь б ю'
            ]}
});

var exclude = [];
var includeOnPosition = [];
var includeWrongPosition = [];

function onKeyPress(button) {
    if(exclude.indexOf(button) < 0) {
        exclude.push(button);
    } else {
        exclude = arrayRemove(exclude, button);
    }
    includeOnPosition = includeOnPosition.filter(item => {
        return item.value != button;
    });
    includeWrongPosition = includeWrongPosition.filter(item => {
        return item.value != button;
    });

    refreshUI();
}

function arrayRemove(arr, value) {
    return arr.filter(function(ele) {
        return ele != value;
    });
}

function refreshUI() {
    $(".hg-button").css("background-color", "#372442");
    exclude.forEach(letter => {
        $("div[data-skbtn="+ letter +"]").css("background-color", "#140029");
    });
    includeWrongPosition.forEach(positionedLetter => {
         $("div[data-skbtn="+ positionedLetter.value +"]").css("background-color", "#cca300");
    });
    includeOnPosition.forEach(positionedLetter => {
         $("div[data-skbtn="+ positionedLetter.value +"]").css("background-color", "#3da35d");
    });
    var usedIndexes = includeOnPosition.map(item => item.index);
    $(".letter-input").each((index, target) => {
        if(usedIndexes.indexOf($(target).attr("x-index")) < 0) {
            $(target).val(null);
        }
    });
    $(".disposed-letter-input").val("");
    includeWrongPosition.forEach(item => {
         var newVal = $(".disposed-letter-input[x-index="+item.index+"]").val() + item.value;
         $(".disposed-letter-input[x-index="+item.index+"]").val(newVal);
    });
}

$(function() {
    refreshUI();
    $(".letter-input").on('input', (event) => {
        var value = event.originalEvent.data;
        var target = event.target;
        var index =  $(target).attr("x-index");
        var positionedValue = { value: value, index: index}
        includeOnPosition = includeOnPosition.filter(item => {
            return item.index != index;
        });
        if(value != "" && value != null) {
            value = value.toLowerCase();
            includeOnPosition.push({ value: value, index: index});
            includeWrongPosition = arrayRemove(includeWrongPosition, positionedValue);
            exclude = arrayRemove(exclude, value);
        }

        refreshUI();
    });
    $(".disposed-letter-input").on('input', (event) => {
        var target = event.target;
        var index =  $(target).attr("x-index");
        includeWrongPosition = includeWrongPosition.filter(item => {
            return item.index != index;
        });
        $(target).val().split("").forEach(value => {
            value = value.toLowerCase();
            var positionedValue = { value: value, index: index};
            if(value != "" && value != null) {
                includeWrongPosition.push(positionedValue);
                includeOnPosition = arrayRemove(includeOnPosition, positionedValue);
                exclude = arrayRemove(exclude, value);
            }
        });

        refreshUI();
    });
    $("#submit-button").click(event => {
        $("#worlds-list").html("<div class='item'><i>Завантаження...</i></div>");
        var iopParam = includeOnPosition.map(item => item.value + ":" + item.index).toString();
        var iwpParam = includeWrongPosition.map(item => item.value + ":" + item.index).toString();
        var params = {
                        exclude: exclude.toString(),
                        includeOnPosition: iopParam,
                        includeWrongPosition: iwpParam
                    };
        $.get("/words", params, function( response ) {
            $("#worlds-list").html("");
            var size = response.words.length > 20 ? 20 : response.words.length;
            for(var i = 0; i < size; i++) {
                $("#worlds-list").append("<div class='item'>"+ response.words[i] +"</div>");
            }
            if(response.words.length > size) {
                $("#worlds-list").append("<div class='item'>...</div>");
            }
        }).fail(() => $("#worlds-list").html("<div class='item'>Помилка сервера</div>"));
    });

    $(".info-button").click(event => {
        var target = event.target;
        var pressed = $(target).hasClass("pressed");
        if(pressed) {
            $(target).removeClass("pressed");
            $(".info-text").css("display", "none");
        } else {
            $(target).addClass("pressed");
            $(".info-text").css("display", "block");
        }
    })
});