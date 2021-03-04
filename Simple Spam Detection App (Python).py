#!/usr/bin/env python3

import sklearn.metrics
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
 
 
 
def get_features(wordlist):
    
    vectorized = CountVectorizer()
    X = vectorized.fit_transform(wordlist)
    return X
 
 
def spam_detection(random_state=0, fraction=1.0):
    import gzip
    hamfile="src/ham.txt.gz"
    spamfile="src/spam.txt.gz"
    
    with gzip.open(hamfile, 'rb') as f:
        ham = f.readlines()
        #ham = [line for line in f]
    ham = ham[0:round(len(ham)*fraction)]
 
 
    
    target = [0]*len(ham)
    with gzip.open(spamfile, 'rb') as f:
        spam = f.readlines()
        #spam = [line for line in f]
    spam = spam[0:round(len(spam)*fraction)]
    
    #test = spam[0:2]
    #print (np.array(test).shape)
    #print (test)
    #test2 = ham[0:2]
    #print (test2)
    
    ham.extend(spam)
    target.extend([1]*len(spam))
    
    features = get_features(ham)
 
    x_train, x_test, y_train, y_test = train_test_split(features, target, test_size=0.25, random_state=random_state)
    model = MultinomialNB()
    model.fit(x_train, y_train)
    
    y_fitted = model.predict(x_test)
    acc=accuracy_score(y_test,y_fitted)
    
    
    return acc, len(y_test), sum(y_test != y_fitted)
 
def main():
    accuracy, total, misclassified = spam_detection()
    print("Accuracy score:", accuracy)
    print(f"{misclassified} messages miclassified out of {total}")
 
if __name__ == "__main__":
    main()