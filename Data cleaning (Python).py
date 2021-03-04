#!/usr/bin/env python3

import pandas as pd

def split_date(df):
    data = df
    data2 = data["Päivämäärä"].str.split(expand=True)
    paiva = ['ma', 'ti', 'ke', 'to', 'pe', 'la', 'su']
    day = ['Mon', 'Tue','Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    kuukausi = ['tammi', 'helmi', 'maalis','huhti','touko','kesä', 'heinä','elo','syys','loka','marras','joulu']
    month = [1,2,3,4,5,6,7,8,9,10,11,12]
    data2[0] = data2[0].replace(paiva, day)
    data2[2]=data2[2].replace(kuukausi, month)
    data2[4]= data2[4].str.split(":", expand=True)
    data2.columns = ['Weekday', 'Day', 'Month', 'Year', 'Hour']
    data2 = data2.astype({"Day" : int, "Year" : int, "Month" : int, "Hour" : int})

    return data2


def split_date_continues():
    data = pd.read_csv("src/Helsingin_pyorailijamaarat.csv", header = 0, sep=";")
    data = data.dropna(axis=0, how='all')
    data = data.dropna(axis=1, how='all')
    date = split_date(data)
    data = data.drop('Päivämäärä', axis=1)
    data = pd.concat([data, date], axis=1)
    data = data[['Weekday', 'Day', 'Month', 'Year','Hour', 'Auroransilta', 'Eteläesplanadi', 'Huopalahti (asema)','Kaisaniemi/Eläintarhanlahti', 'Kaivokatu', 'Kulosaaren silta et.','Kulosaaren silta po. ', 'Kuusisaarentie', 'Käpylä, Pohjoisbaana','Lauttasaaren silta eteläpuoli', 'Merikannontie','Munkkiniemen silta eteläpuoli', 'Munkkiniemi silta pohjoispuoli','Heperian puisto/Ooppera', 'Pitkäsilta itäpuoli','Pitkäsilta länsipuoli', 'Lauttasaaren silta pohjoispuoli','Ratapihantie', 'Viikintie', 'Baana']]

    return data

def bicycle_timeseries():
    
    data = split_date_continues()
    data["Päivämäärä"] = pd.to_datetime(data[["Year", "Month", "Day", "Hour"]])
    data = data.drop(columns=["Year", "Month", "Day", "Hour", "Weekday"])
    data = data.set_index("Päivämäärä")

    return data


def main():
    print(bicycle_timeseries().columns)

if __name__ == "__main__":
    main()
