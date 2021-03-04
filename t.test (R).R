library(datasets)
library(MASS)

# a) 
x <- NULL
y <- NULL

View(iris)

iris$Sepal.Width[iris$Species == "setosa"]

x <- iris$Sepal.Width[iris$Species == "setosa"]
y <- iris$Sepal.Width[iris$Species == "versicolor"]

t.test(x, y, var.equal = TRUE)[3]


protest <- function(x1,x2,n1,n2){
  
  p1 <- x1/n1
  p2 <- x2/n2
  phattu <- (x1+x2)/(n1+n2)
  
  TS <- (p1-p2)/sqrt((1/n1+1/n2)*phattu*(1-phattu))
  
  return(TS)

}

qnorm(mean=0, sd=1, p=0.025, lower.tail=FALSE)

protest2 <- function(x1,x2,n1,n2,alpha){
  
  p1 <- x1/n1
  p2 <- x2/n2
  phattu <- (x1+x2)/(n1+n2)
  
  TS <- (p1-p2)/sqrt((1/n1+1/n2)*phattu*(1-phattu))
  
  kv <- qnorm(mean=0, sd=1, p=alpha/2, lower.tail=FALSE)
  
  if(abs(TS) > kv){
    palautus <- list(statistic = TS, rejected = TRUE)
  } else {
    palautus <- list(statistic = TS, rejected = FALSE)
  }
  
  return(palautus)
}

protest2(4,2,10,14,0.05)

View(anorexia)

anorexia2 <- anorexia
Prewt_kg <- anorexia2[,2]*0.45359237
Postwt_kg <- anorexia2[,3]*0.45359237

anorexia2 <- cbind(anorexia2,Prewt_kg)
anorexia2 <- cbind(anorexia2,Postwt_kg)

class(pvals1)
str(pvals1)

meanvals1 <- mean(anorexia2[,4][anorexia2[,1] == "Cont"])
meanvals1 <- c(meanvals1, mean(anorexia2[,4][anorexia2[,1] == "CBT"]))  
meanvals1 <- c(meanvals1, mean(anorexia2[,4][anorexia2[,1] == "FT"])) 

pvals1 <- t.test(anorexia2[,4][anorexia2[,1] == "Cont"], anorexia2[,4][anorexia2[,1] == "CBT"])$p.value
pvals1 <- c(pvals1, t.test(anorexia2[,4][anorexia2[,1] == "Cont"], anorexia2[,4][anorexia2[,1] == "FT"])$p.value)
pvals1 <- c(pvals1, t.test(anorexia2[,4][anorexia2[,1] == "FT"], anorexia2[,4][anorexia2[,1] == "CBT"])$p.value)

meanvals2 <- mean(anorexia2[,5][anorexia2[,1] == "Cont"])
meanvals2 <- c(meanvals2, mean(anorexia2[,5][anorexia2[,1] == "CBT"]))  
meanvals2 <- c(meanvals2, mean(anorexia2[,5][anorexia2[,1] == "FT"])) 

pvals2 <- t.test(anorexia2[,5][anorexia2[,1] == "CBT"], anorexia2[,5][anorexia2[,1] == "Cont"])$p.value
pvals2 <- c(pvals2, t.test(anorexia2[,5][anorexia2[,1] == "FT"],anorexia2[,5][anorexia2[,1] == "Cont"])$p.value)

rm(pvals2)

rm(meanvals2)

mean(anorexia2[,5][anorexia2[,1] == "Cont"])
mean(anorexia2[,5][anorexia2[,1] == "CBT"])
mean(anorexia2[,5][anorexia2[,1] == "FT"])

cont <- t.test(anorexia2[,4][anorexia2[,1] == "Cont"], anorexia2[,5][anorexia2[,1] == "Cont"], paired = TRUE)$p.value
cbt <- t.test(anorexia2[,4][anorexia2[,1] == "CBT"], anorexia2[,5][anorexia2[,1] == "CBT"], paired = TRUE)$p.value
ft <- t.test(anorexia2[,4][anorexia2[,1] == "FT"], anorexia2[,5][anorexia2[,1] == "FT"], paired = TRUE)$p.value

pvals3 <- c(cont, cbt, ft)




