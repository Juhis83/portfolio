#!/usr/bin/env python3

from collections import Counter
import urllib.request
from lxml import etree

import numpy as np

from sklearn.naive_bayes import MultinomialNB
from sklearn.model_selection import cross_val_score
from sklearn import model_selection

alphabet="abcdefghijklmnopqrstuvwxyzäö-"
alphabet_set = set(alphabet)

# Returns a list of Finnish words
def load_finnish():
    finnish_url="https://www.cs.helsinki.fi/u/jttoivon/dap/data/kotus-sanalista_v1/kotus-sanalista_v1.xml"
    filename="src/kotus-sanalista_v1.xml"
    load_from_net=False
    if load_from_net:
        with urllib.request.urlopen(finnish_url) as data:
            lines=[]
            for line in data:
                lines.append(line.decode('utf-8'))
        doc="".join(lines)
    else:
        with open(filename, "rb") as data:
            doc=data.read()
    tree = etree.XML(doc)
    s_elements = tree.xpath('/kotus-sanalista/st/s')
    return list(map(lambda s: s.text, s_elements))

def load_english():
    with open("src/words", encoding="utf-8") as data:
        lines=map(lambda s: s.rstrip(), data.readlines())
    return lines

def get_features(a):
    letter_counts = np.zeros((len(a),len(alphabet)))
    alphabet_num_dict = dict(zip(list(alphabet),range(0,len(alphabet))))

    for index,word in enumerate(a):
        word = word.lower()   
        listofchars = list(word)
        
        for character in listofchars:
            letter_counts[index,alphabet_num_dict[character]] = letter_counts[index,alphabet_num_dict[character]] + 1

    return letter_counts

def contains_valid_chars(s):
    aux = True
    s_list = list(s)
    alphabet_list = list(alphabet)

    for c in s_list:
        if c in alphabet_list:
            aux = True
        else:
            aux = False
            break
    
    return aux

def remove_unvalid_chars(a):
    test = list(map(contains_valid_chars,a))
    return a[test]

def clean_finnish(lines):
    a = np.array(lines)
    a = np.char.lower(a)
    a = remove_unvalid_chars(a)
 
    return a
 
def clean_english(lines):
    ret = []
    for word in lines:
        if word[0].isupper():
            continue
        ret.append(word)
 
    a = np.array(clean_finnish(ret))
    a = np.char.lower(a)
    a = remove_unvalid_chars(a)
 
    return a

def get_features_and_labels():
    a1 = clean_finnish(load_finnish())
    a2 = clean_english(load_english())
 
    features = np.concatenate((a1,a2),axis=0)
    features = features[:,np.newaxis]
 
    a1_labels = np.full((len(a1),1),0)  # Label Finnish words with 0
    a2_labels = np.full((len(a2),1),1) #Label English words with 1
 
    labels = np.concatenate((a1_labels,a2_labels),axis=0)
 
    combined = np.hstack((features,labels))
 
    feature_matrix = get_features(combined[:,0])
    target_vector = labels
    return (feature_matrix,target_vector)


def word_classification():
    X, y = get_features_and_labels()
    model = MultinomialNB()
    # xtrain,xtest,ytrain,ytest = model_selection.train_test_split(X,y)
    y_ravel = y.ravel()
    # model.fit(X,y_ravel)
 
    cv_generator = model_selection.KFold(n_splits=5, shuffle=True, random_state=0)
 
    cross_validation = cross_val_score(model,X,y_ravel,cv=cv_generator)
    
    return cross_validation

def main():
    
    print("Accuracy scores are:", word_classification())

if __name__ == "__main__":
    main()
