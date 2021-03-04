library(MASS)
survey
str(survey)

taulukko <- table(survey$Fold, survey$Sex)
tulokset <- chisq.test(taulukko)
tulokset$p.value
a1 <- tulokset$statistic
a2 <- tulokset$p.value

taulukko2 <- table(survey$Clap, survey$Sex)
tulokset2 <- chisq.test(taulukko2)
tulokset2$p.value
a3 <- tulokset2$statistic
a4 <- tulokset2$p.value

1-pchisq(4.36, 4)
#sama toisin:
pchisq(4.36, 4, lower.tail = F)


mytest <- function(freqs, ports){
  
  if(length(freqs) == length(ports) & sum(ports) == 1 ){
    
    k <- length(freqs)
    frekvenssit <- sum(freqs)*ports
    ts <- sum((freqs-frekvenssit)^2/frekvenssit)
    p_arvo <- 1-pchisq(ts, k-1) 
    
    return(list(frekvenssit, ts, p_arvo))
    
  } else {
   
    return(FALSE) 
   
  }
  
}


freqs <- c(92, 20, 4, 84)
ports <- c(0.41, 0.09, 0.04, 0.46)

mytest(freqs, ports)
length(freqs) == length(ports)
sum(ports) == 1



Titanic
str(Titanic)
Titanic[, 1, 2, ]

b1 <- Titanic[1,2,2,2]/sum(Titanic[1,2,2,])
b2 <- Titanic[1,1,2,2]/sum(Titanic[1,1,2,])

b3 <- Titanic[4,2,2,2]/sum(Titanic[4,2,2,])
b4 <- Titanic[4,1,2,2]/sum(Titanic[4,1,2,])

b5 <- sum(Titanic[3,,,2])/sum(Titanic[3,,,])

