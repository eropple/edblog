$(document).foundation();

$(document).ready(function() {
  $("pre > code").each(function(elem_idx, elem) {
    var $elem = $(elem);
    var attr_class = $elem.attr("class");
    if (attr_class && attr_class.length > 0) {
      $.each(attr_class.split(" "), function(cls_idx, cls) {
        $elem.addClass("language-" + cls);
      })
    }
  })
})

$(document).ready(function() {
  if ($( window ).width() > 1024) { 
    $("article").each(function(article_idx, article) {
      var body = $(article).find(".body");
      var endnotes = $(article).find(".endnotes");
      var sidenotes = $(article).find(".sidenotes");
      var hash = window.location.hash;
    
      var offset_top = body.offset().top;
      $(endnotes).children("dl").each(function(endnote_idx, endnote) {
        var new_endnote = $(endnote).clone();
      
        var anchor_name = "a#a" + article_idx + "e" + (endnote_idx + 1) + "-a";
        var anchor = $(article).find(anchor_name);
        var anchor_offset_top = $(anchor).offset().top;
      
        var delta_y = anchor_offset_top - offset_top;
      
        $(new_endnote).css("position", "relative").css("top", + delta_y);
      
        $(new_endnote).appendTo(sidenotes);
      });
  
  
      endnotes.animate({ opacity: 0, height: 0 }, { duration: 1000, queue: false, complete: function() {
          $(this).remove();
          window.location.hash = hash;
        }
      });
      sidenotes.animate({ opacity: 1 }, { duration: 1000, queue: false });
    });
  }
});