import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import io
import sys

fileName = sys.argv[1] if len(sys.argv) > 1 else input("Enter the path to the data file: ")
prefix = sys.argv[2] if len(sys.argv) > 2 else input("Enter the prefix for output files (e.g., 'experiment1_'): ")

print("Processing " + fileName + "...")

# 1. Read the file, handling the Windows PowerShell UTF-16 encoding
try:
    with open(fileName, 'r', encoding='utf-16') as file:
        lines = file.readlines()
except UnicodeError:
    # Fallback just in case it was saved in standard UTF-8
    with open(fileName, 'r', encoding='utf-8') as file:
        lines = file.readlines()

# 2. Automatically skip the Java setup text at the top of the file
csv_lines = []
recording_csv = False

for line in lines:
    if line.startswith("LoadFactor"):
        recording_csv = True # We found the header, start capturing!
    if recording_csv and line.strip(): # Capture non-empty lines
        csv_lines.append(line)

if not csv_lines:
    print("Error: Could not find the CSV data in the file.")
    exit()

# 3. Load the clean text into pandas
df = pd.read_csv(io.StringIO(''.join(csv_lines)))

# 4. Plot Load Factor vs Insertion Time
plt.figure(figsize=(20, 12))
sns.lineplot(data=df, x='LoadFactor', y='InsertTime_ms', hue='Algorithm', marker='o')
plt.title('Insertion Time vs. Load Factor')
plt.xlabel('Load Factor')
plt.ylabel('Total Insertion Time (ms)')
plt.grid(True)
plt.savefig('plots/' + prefix + '_insertion_time.png')

# 5. Plot Load Factor vs Failures
plt.figure(figsize=(20, 12))
sns.lineplot(data=df, x='LoadFactor', y='Failures', hue='Algorithm', marker='s')
plt.title('Insertion Failures vs. Load Factor ')
plt.xlabel('Load Factor')
plt.ylabel('Number of Failures')
plt.grid(True)
plt.savefig('plots/' + prefix + '_failures.png')

# 6. Plot Load Factor vs Delete Time
plt.figure(figsize=(20, 12))
sns.lineplot(data=df, x='LoadFactor', y='DeleteTime_ms', hue='Algorithm', marker='s')
plt.title('Delete Time vs. Load Factor ')
plt.xlabel('Load Factor')
plt.ylabel('Total Delete Time (ms)')
plt.grid(True)
plt.savefig('plots/' + prefix + '_delete_time.png')

# 7. Plot Load Factor vs Lookup Time
plt.figure(figsize=(20, 12))
sns.lineplot(data=df, x='LoadFactor', y='LookupTime_ms', hue='Algorithm', marker='s')
plt.title('Lookup Time vs. Load Factor ')
plt.xlabel('Load Factor')
plt.ylabel('Total Lookup Time (ms)')
plt.grid(True)
plt.savefig('plots/' + prefix + '_lookup_time.png')

print("Success! Graphs generated.")