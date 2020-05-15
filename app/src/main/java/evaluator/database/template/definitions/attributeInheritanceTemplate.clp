(defrule {{module}}::inherit-{{template1.split("::")[1]}}-{{copySlotName1}}-TO-{{template2.split("::")[1].trim()}}

(declare (no-loop TRUE))



{% if  copySlotType1.equalsIgnoreCase("slot")  %}
     ?sub <- ({{template1}} ({{copySlotName1}} ?x&~nil)
{% else %}
     ?sub <- ({{template1}} ({{copySlotName1}} $?x&:(> (length$ $?x) 0))
{% endif %}


{% elseif  matchingSlotType1.equalsIgnoreCase("slot")  %}
     ({{matchingSlotName1}} ?id&~nil) )
{% else %}
     ({{matchingSlotName1}} $?id&:(> (length$ $?id) 0)) )
{% endif %}

 ?old <- ({{template2}}
{% elseif  matchingSlotType1.equalsIgnoreCase("slot")  %}
    ({{matchingSlotName2}} ?id) (factHistory ?fh)
{% else %}
    ({{matchingSlotName2}} $?id) (factHistory ?fh)
{% endif %}


{% elseif  matchingSlotType1.equalsIgnoreCase("slot")  %}
    ({{copySlotName2}} nil)
{% else %}
    ({{copySlotName2}} $?x&:(eq (length$ $?x) 0))
{% endif %}

) => (modify ?old ({{copySlotName2}} ?x)(factHistory (str-cat \"{R\" (?*rulesMap* get {{module}}::inherit-{{template1.split("::")[1]}}-{{copySlotName1}}-TO-{{template2.split("::")[1]}}) \" \" ?fh \" S\" (call ?sub getFactId) \"}\")
)))