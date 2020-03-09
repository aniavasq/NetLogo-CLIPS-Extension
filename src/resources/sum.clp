(deftemplate sum
  (slot total)
)

(deffacts sum-init
  (sum (total 0)))

; (defrule assert-vals
;  =>
;  (assert (vals 1 2 3 4))
; )

(defrule sum
  ?f1 <- (vals ?v $?l)
  ?f2 <- (sum (total ?total))
  =>
  (retract ?f1)
  (assert (vals $?l))
  (modify ?f2 (total (+ ?total ?v)))
)