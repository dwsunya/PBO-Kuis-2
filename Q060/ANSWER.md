# Q060 — Your answer

## (a) The trace

TODO(Q060): give the label and `handled` after each of the nine steps.

|No|Step|Handled|Label|
|-|-|-|-|
|0|start|0|(no quote yet)|
|1|type 4.2|0|(no quote yet)|
|2|click Quote|1|WF 4.5 kg: Rp31,500, ready in 48 h|
|3|tick Express|2|WF 4.5 kg express: Rp47,250, ready in 12 h|
|4|choose WDI|3|WDI 4.5 kg express: Rp67,500, ready in 18 h|
|5|type abc|3|WDI 4.5 kg express: Rp67,500, ready in 18 h|
|6|press Enter|4|For input string: "abc"|
|7|untick Express|5|For input string: "abc"|
|8|choose IR|6|For input string: "abc"|
|9|type 0.4, click|7|IR 2.0 kg: Rp10,000, ready in 24 h|

## (b) The stale label

TODO(Q060): why does step 5 leave a label that contradicts the field, and what event would
you listen to in order to fix it?

```
Step 5 leaves the label inconsistent with the field's content because typing "abc" into the field only changes the text without triggering the existing listeners. Consequently, the label continues to display the result of the previous input.

The actions that will trigger the listener and update the label are pressing Enter within the field or clicking the Quote button. Both actions invoke refresh(true), which causes the model to recalculate based on the current text in the field, resulting in the label displaying an error message for the input abc.
```