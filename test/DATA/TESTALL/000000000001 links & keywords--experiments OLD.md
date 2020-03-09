NOTE: text of the form #whatamI (not case-sensitive) establishes a *tag*, not a link. Typing this text in the Omnibar will retrieve any note that contains "#whatami" (even without the the leading '#').


link to 999900000002 (nope)
does this work?: *** [[999900000002]]: additional note *** (nope)
link to [[999900000002]] (yep)
does this work?: [[999900000002] wakka] (nope)
does this work?: >[[999900000001]]: additional note< (yep)
does this work?: >>[[999900000002]]: additional note<< (yep)
does this work?: ---[[999900000002]]: additional note--- (yep)
does this work?: >> [[999900000002]]: additional note << (yep)
does this work?: << [[999900000002]]: additional note >> (yep)
does this work?: ***[[999900000002]]: additional note*** (yep)
does this work?: **[[999900000002]]: additional note** (yep)
does this work?: *[[999900000002]]: additional note* (yep)
dymop

=====================================

## Here is the one I like the best (form is '***GW_style_tag:[[]]***')
---
*parent-to-child link (i.e., default, has no GW_style_tag*)
Use a Wiki-Link: create a link that leads nowhere, click on it to follow the link. There’ll be no note to show, but the Omnibar ***[[999900000002]]*** contains the link now. Then create a note from the Omnibar as you would above.
---

---
*GW_style_tag, example 1*
Use a Wiki-Link: create a link that leads nowhere, click on it to follow the link. There’ll be no note to show, but the Omnibar **parent:[[999900000002]]** contains the link now. Then create a note from the Omnibar as you would above.
---

---
*GW_style_tag, example 2*
Use a Wiki-Link: create a link that leads nowhere, click on it to follow the link. There’ll be no note to show, but the Omnibar ***source:[[999900000002]]*** contains the link now. Then create a note from the Omnibar as you would above.
---

### Valid GW_style_tags

<<no GW_style_tag>>   this is the default parent-to-child link
parent:               this is the child-to-parent link
source:               pointer to a literature card--i.e., a card that
                      gives definitive info on a URL, book name, etc. 

=============== experiments on link format ===============

The purpose is to create tags that aren't CamelCased or 'justElided', so that a search for just the words with their spaces can be found via the tag or just a search for the bare words.



