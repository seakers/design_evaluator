

(defrule FUZZY::numerical-to-fuzzy-{{item.name()}}

    ?m <- ({{template}}

{% if item.parameter().equalsIgnoreCase("all") %}
            ({{item.name()}}# ?num&~nil)
{% else %}
            (Parameter "{{item.parameter()}}")
            ({{item.name().substring(0, item.name().length() - 1 )}}# ?num&~nil)
{% endif %}
    ({{item.name()}} nil)
    (factHistory ?fh))
    =>


    (bind ?value (numerical-to-fuzzy
                    ?num
                    (create${% for value in item.values() %} {{value.value()}}{% endfor %})
                    (create${% for value in item.values() %} {{value.minimum()}}{% endfor %})
                    (create${% for value in item.values() %} {{value.maximum()}}{% endfor %})))
    (modify ?m ({{item.name()}} ?value) (factHistory (str-cat "{R" (?*rulesMap* get FUZZY::numerical-to-fuzzy-{{item.name()}}) " " ?fh "}")))
)