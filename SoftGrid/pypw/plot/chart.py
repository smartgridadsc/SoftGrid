import plotly.plotly as py
import plotly.graph_objs as go
# Sign in to plotly
py.sign_in('prageeth', 'sithumina') # Replace the username, and API key with your credentials.

# Create a simple chart..
trace = go.Bar(x=[2, 4, 6], y= [10, 12, 15])
data = [trace]
layout = go.Layout(title='A Simple Plot', width=800, height=640)
fig = go.Figure(data=data, layout=layout)
py.image.save_as(fig, filename='asimpleplot.png')
