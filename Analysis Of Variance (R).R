library(MASS)
library(tidyverse)

View(coop)

seq(1,nrow(coop),2)

coop2 <- coop[seq(1,nrow(coop),2),]

kons <- coop2$Conc[coop2$Spc == "S5"]

lab <- coop2$Lab[coop2$Spc == "S5"]

oneway.test(kons ~ lab, var.equal=FALSE)[1]
oneway.test(kons ~ lab, var.equal=FALSE)[3]

bat <- coop2$Bat[coop2$Spc == "S5"]

tulokset <- aov(kons ~ lab + bat)

tulokset2 <- unlist(summary(tulokset)) 
names(tulokset2)
tulokset2["F value1"]
tulokset2["F value2"]
tulokset2["Pr(>F)1"]
tulokset2["Pr(>F)2"]

coop3 <- coop2 %>% mutate(uusi = ifelse(str_detect(coop2$Lab, "L1|L2"), "yes", "no"))

coop3 <- coop3 %>% mutate(suuruus = ifelse(between(coop3$Conc, 0.0, 1.0), "pieni", "suuri"))
coop3 <- coop3 %>% mutate(suuruus = ifelse(between(coop3$Conc, 0.0, 1.0), "pieni", ifelse(between(coop3$Conc, 1.0, 2.0), "keskikoko", "suuri")))

str(coop3)
class(coop3)

coop3$suuruus <- as.factor(coop3$suuruus)
coop3$suuruus <- as.character(coop3$suuruus)

str(coop3)

testi <- c(3,4,5)
sum(testi-2)^2

(testi - 2)^2


# Week 2 Exercise 2
one_way_anova <- function(m, n, sample_means, sample_vars) {
  x__ <- 1/m * sum(sample_means)
  
  S_squared <- (sum((sample_means - x__)^2)) / (m - 1)
  
  TS <- (n * S_squared) / (sum(sample_vars)/m)
  
  p_arvo <- 1 - pf(TS, m-1, m*(n-1))
  
  palautus <- list(statistic = TS, pvalue = p_arvo)
  
  return(palautus)
  
}


means <- c(240.6, 235.6, 253.6)
vars <- c(287.8, 59.3, 150.8)

one_way_anova(3, 5, means, vars)


## 3 a)

boxplot(coop2[coop2$Spc == "S5",]$Conc~coop2[coop2$Spc == "S5",]$Lab)


## 3 b)

boxplot(coop2[coop2$Spc == "S5",]$Conc~coop2[coop2$Spc == "S5",]$Bat)

## 3 c)

set.seed(1)
dat <- matrix(rnorm(300*20), nrow=300)
sample_means <- matrix(rowMeans(dat), nrow=100)
sample_vars <- matrix(apply(dat, 1, var), nrow=100)

one_way_anova(20, 300, sample_means[1,], sample_vars[1,])$pvalue

pvalues <- replicate(100, 0)
class(pvalues)
typeof(pvalues)


for(i in 1:100){
  pvalues[i] <- one_way_anova(3, 20, sample_means[i,], sample_vars[i,])$pvalue
}

plot(pvalues, col = ifelse(pvalues < 0.05, "red", "blue"))
legend("right", legend = c("p-arvo yli 0.05", "p-arvo alle 0.05"), col = c("blue", "red"), cex=1.2, pch=1)

2^8











